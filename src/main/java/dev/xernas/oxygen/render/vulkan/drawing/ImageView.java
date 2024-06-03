package dev.xernas.oxygen.render.vulkan.drawing;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class ImageView implements IOxygenLogic {

    private final Device device;
    private final long vkImage;
    private final ImageViewData imageViewData;
    private long vkImageView;

    public ImageView(Device device, long vkImage, ImageViewData imageViewData) {
        this.device = device;
        this.vkImage = vkImage;
        this.imageViewData = imageViewData;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .image(vkImage)
                    .viewType(imageViewData.viewType)
                    .format(imageViewData.format)
                    .subresourceRange(it -> it
                            .aspectMask(imageViewData.aspectMask)
                            .baseMipLevel(0)
                            .levelCount(imageViewData.mipLevels)
                            .baseArrayLayer(imageViewData.baseArrayLayer)
                            .layerCount(imageViewData.layerCount));

            LongBuffer pImageView = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreateImageView(device.getVkDevice(), viewCreateInfo, null, pImageView),
                    "Failed to create image view");
            vkImageView = pImageView.get(0);
        }
    }

    public void cleanup() {
        vkDestroyImageView(device.getVkDevice(), vkImageView, null);
    }

    public long getVkImageView() {
        return vkImageView;
    }

    public static class ImageViewData {
        private int aspectMask;
        private int baseArrayLayer;
        private int format;
        private int layerCount;
        private int mipLevels;
        private int viewType;

        public ImageViewData() {
            this.baseArrayLayer = 0;
            this.layerCount = 1;
            this.mipLevels = 1;
            this.viewType = VK_IMAGE_VIEW_TYPE_2D;
        }

        public ImageView.ImageViewData aspectMask(int aspectMask) {
            this.aspectMask = aspectMask;
            return this;
        }

        public ImageView.ImageViewData baseArrayLayer(int baseArrayLayer) {
            this.baseArrayLayer = baseArrayLayer;
            return this;
        }

        public ImageView.ImageViewData format(int format) {
            this.format = format;
            return this;
        }

        public ImageView.ImageViewData layerCount(int layerCount) {
            this.layerCount = layerCount;
            return this;
        }

        public ImageView.ImageViewData mipLevels(int mipLevels) {
            this.mipLevels = mipLevels;
            return this;
        }

        public ImageView.ImageViewData viewType(int viewType) {
            this.viewType = viewType;
            return this;
        }
    }

}
