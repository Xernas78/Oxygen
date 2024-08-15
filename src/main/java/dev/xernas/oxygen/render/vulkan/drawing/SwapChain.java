package dev.xernas.oxygen.render.vulkan.drawing;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.Window;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.device.PhysicalDevice;
import dev.xernas.oxygen.render.vulkan.command.PresentQueue;
import dev.xernas.oxygen.render.vulkan.command.Queue;
import dev.xernas.oxygen.render.vulkan.drawing.surface.Surface;
import dev.xernas.oxygen.render.vulkan.drawing.surface.SurfaceFormat;
import dev.xernas.oxygen.render.vulkan.sync.Semaphore;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

public class SwapChain implements IOxygenLogic {

    private final Device device;
    private final Surface surface;
    private final Window window;
    private final int requestedImages;
    private final boolean vsync;
    private final PresentQueue presentQueue;
    private final Queue[] concurrentQueues;

    private SurfaceFormat surfaceFormat;
    private ImageView[] imageViews;
    private VkExtent2D swapChainExtent;
    private long vkSwapChain;
    private SyncSemaphores[] syncSemaphoresList;

    private int currentFrame;

    public SwapChain(Device device, Surface surface, Window oxygen, int requestedImages, boolean vsync, PresentQueue presentQueue, Queue[] concurrentQueues) {
        this.device = device;
        this.surface = surface;
        this.window = oxygen;
        this.requestedImages = requestedImages;
        this.vsync = vsync;
        this.presentQueue = presentQueue;
        this.concurrentQueues = concurrentQueues;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            PhysicalDevice physicalDevice = device.getPhysicalDevice();

            // Get surface capabilities
            VkSurfaceCapabilitiesKHR surfCapabilities = VkSurfaceCapabilitiesKHR.calloc(stack);
            VulkanUtils.vkCheck(KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice.getVkPhysicalDevice(),
                    surface.getVkSurface(), surfCapabilities), "Failed to get surface capabilities");

            int numImages = calcNumImages(surfCapabilities, requestedImages);
            surfaceFormat = calcSurfaceFormat(stack, physicalDevice, surface);
            swapChainExtent = calcSwapChainExtent(window, surfCapabilities);

            VkSwapchainCreateInfoKHR vkSwapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc(stack)
                    .sType(KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface.getVkSurface())
                    .minImageCount(numImages)
                    .imageFormat(surfaceFormat.format())
                    .imageColorSpace(surfaceFormat.colorSpace())
                    .imageExtent(swapChainExtent)
                    .imageArrayLayers(1)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                    .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .preTransform(surfCapabilities.currentTransform())
                    .compositeAlpha(KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                    .clipped(true);
            if (vsync) {
                vkSwapchainCreateInfo.presentMode(KHRSurface.VK_PRESENT_MODE_FIFO_KHR);
            } else {
                vkSwapchainCreateInfo.presentMode(KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR);
            }

            int numQueues = concurrentQueues != null ? concurrentQueues.length : 0;
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < numQueues; i++) {
                Queue queue = concurrentQueues[i];
                if (queue.getQueueFamilyIndex() != presentQueue.getQueueFamilyIndex()) {
                    indices.add(queue.getQueueFamilyIndex());
                }
            }
            if (!indices.isEmpty()) {
                IntBuffer intBuffer = stack.mallocInt(indices.size() + 1);
                indices.forEach(intBuffer::put);
                intBuffer.put(presentQueue.getQueueFamilyIndex()).flip();
                vkSwapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT)
                        .queueFamilyIndexCount(intBuffer.capacity())
                        .pQueueFamilyIndices(intBuffer);
            } else {
                vkSwapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            }
            syncSemaphoresList = new SyncSemaphores[numImages];
            for (int i = 0; i < numImages; i++) {
                SyncSemaphores syncSemaphores = new SyncSemaphores(device);
                syncSemaphores.init();
                syncSemaphoresList[i] = syncSemaphores;
            }
            currentFrame = 0;

            LongBuffer pSwapChain = stack.mallocLong(1);
            VulkanUtils.vkCheck(KHRSwapchain.vkCreateSwapchainKHR(device.getVkDevice(), vkSwapchainCreateInfo, null, pSwapChain),
                    "Failed to create swap chain");
            vkSwapChain = pSwapChain.get(0);

            imageViews = createImageViews(stack, device, vkSwapChain, surfaceFormat.format());
        }
    }

    public void cleanup() {
        swapChainExtent.free();
        Arrays.asList(imageViews).forEach(ImageView::cleanup);
        KHRSwapchain.vkDestroySwapchainKHR(device.getVkDevice(), vkSwapChain, null);
        Arrays.asList(syncSemaphoresList).forEach(SyncSemaphores::cleanup);
    }

    public boolean acquireNextImage() throws OxygenException {
        boolean resize = false;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer ip = stack.mallocInt(1);
            int err = KHRSwapchain.vkAcquireNextImageKHR(device.getVkDevice(), vkSwapChain, ~0L,
                    syncSemaphoresList[currentFrame].imgAcquisitionSemaphore().getVkSemaphore(), MemoryUtil.NULL, ip);
            if (err == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
                resize = true;
            } else if (err != VK_SUCCESS) {
                throw new OxygenException("Failed to acquire image: " + err);
            }
            currentFrame = ip.get(0);
        }

        return resize;
    }

    public boolean presentImage(Queue queue) {
        boolean resize = false;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPresentInfoKHR present = VkPresentInfoKHR.calloc(stack)
                    .sType(KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                    .pWaitSemaphores(stack.longs(
                            syncSemaphoresList[currentFrame].renderCompleteSemaphore().getVkSemaphore()))
                    .swapchainCount(1)
                    .pSwapchains(stack.longs(vkSwapChain))
                    .pImageIndices(stack.ints(currentFrame));

            int err = KHRSwapchain.vkQueuePresentKHR(queue.getVkQueue(), present);
            if (err == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
                resize = true;
            } else if (err != VK_SUCCESS) {
                throw new RuntimeException("Failed to present KHR: " + err);
            }
        }
        currentFrame = (currentFrame + 1) % imageViews.length;
        return resize;
    }

    public Device getDevice() {
        return device;
    }

    public SurfaceFormat getSurfaceFormat() {
        return surfaceFormat;
    }

    public VkExtent2D getSwapChainExtent() {
        return swapChainExtent;
    }

    public ImageView[] getImageViews() {
        return imageViews;
    }

    public SyncSemaphores[] getSyncSemaphoresList() {
        return syncSemaphoresList;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    private ImageView[] createImageViews(MemoryStack stack, Device device, long swapChain, int format) throws OxygenException {
        ImageView[] result;

        IntBuffer ip = stack.mallocInt(1);
        VulkanUtils.vkCheck(KHRSwapchain.vkGetSwapchainImagesKHR(device.getVkDevice(), swapChain, ip, null),
                "Failed to get number of surface images");
        int numImages = ip.get(0);

        LongBuffer swapChainImages = stack.mallocLong(numImages);
        VulkanUtils.vkCheck(KHRSwapchain.vkGetSwapchainImagesKHR(device.getVkDevice(), swapChain, ip, swapChainImages),
                "Failed to get surface images");

        result = new ImageView[numImages];
        ImageView.ImageViewData imageViewData = new ImageView.ImageViewData().format(format).aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
        for (int i = 0; i < numImages; i++) {
            ImageView newImageView = new ImageView(device, swapChainImages.get(i), imageViewData);
            newImageView.init();
            result[i] = newImageView;
        }

        return result;
    }

    private int calcNumImages(VkSurfaceCapabilitiesKHR surfCapabilities, int requestedImages) {
        int maxImages = surfCapabilities.maxImageCount();
        int minImages = surfCapabilities.minImageCount();
        int result = minImages;
        if (maxImages != 0) {
            result = Math.min(requestedImages, maxImages);
        }
        result = Math.max(result, minImages);

        return result;
    }

    private SurfaceFormat calcSurfaceFormat(MemoryStack stack, PhysicalDevice physicalDevice, Surface surface) throws OxygenException {
        int imageFormat;
        int colorSpace;
        IntBuffer ip = stack.mallocInt(1);
        VulkanUtils.vkCheck(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.getVkPhysicalDevice(),
                surface.getVkSurface(), ip, null), "Failed to get the number surface formats");
        int numFormats = ip.get(0);
        if (numFormats <= 0) {
            throw new RuntimeException("No surface formats retrieved");
        }

        VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.calloc(numFormats, stack);
        VulkanUtils.vkCheck(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.getVkPhysicalDevice(),
                surface.getVkSurface(), ip, surfaceFormats), "Failed to get surface formats");

        imageFormat = VK_FORMAT_B8G8R8A8_SRGB;
        colorSpace = surfaceFormats.get(0).colorSpace();
        for (int i = 0; i < numFormats; i++) {
            VkSurfaceFormatKHR surfaceFormatKHR = surfaceFormats.get(i);
            if (surfaceFormatKHR.format() == VK_FORMAT_B8G8R8A8_SRGB &&
                    surfaceFormatKHR.colorSpace() == KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
                imageFormat = surfaceFormatKHR.format();
                colorSpace = surfaceFormatKHR.colorSpace();
                break;
            }
        }
        return new SurfaceFormat(imageFormat, colorSpace);
    }

    private VkExtent2D calcSwapChainExtent(Window window, VkSurfaceCapabilitiesKHR surfCapabilities) {
        VkExtent2D result = VkExtent2D.calloc();
        if (surfCapabilities.currentExtent().width() == 0xFFFFFFFF) {
            int width = Math.min(window.getWidth(), surfCapabilities.maxImageExtent().width());
            width = Math.max(width, surfCapabilities.minImageExtent().width());

            int height = Math.min(window.getHeight(), surfCapabilities.maxImageExtent().height());
            height = Math.max(height, surfCapabilities.minImageExtent().height());

            result.width(width);
            result.height(height);
        } else {
            result.set(surfCapabilities.currentExtent());
        }
        return result;
    }

    public record SyncSemaphores(Semaphore imgAcquisitionSemaphore, Semaphore renderCompleteSemaphore) implements IOxygenLogic {

        public SyncSemaphores(Device device) {
            this(new Semaphore(device), new Semaphore(device));
        }


        @Override
        public void init() throws OxygenException {
            imgAcquisitionSemaphore.init();
            renderCompleteSemaphore.init();
        }

        public void cleanup() {
            imgAcquisitionSemaphore.cleanup();
            renderCompleteSemaphore.cleanup();
        }
    }

}
