package dev.xernas.oxygen.render.vulkan.command;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import static org.lwjgl.vulkan.VK10.*;

public class CommandBuffer implements IOxygenLogic {

    private final CommandPool commandPool;
    private final boolean primary;
    private final boolean oneTimeSubmit;
    private VkCommandBuffer vkCommandBuffer;

    public CommandBuffer(CommandPool commandPool, boolean primary, boolean oneTimeSubmit) {
        this.commandPool = commandPool;
        this.primary = primary;
        this.oneTimeSubmit = oneTimeSubmit;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDevice vkDevice = commandPool.getDevice().getVkDevice();
            VkCommandBufferAllocateInfo pCmdBufAllocateInfo = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(commandPool.getVkCommandPool())
                    .level(primary ? VK_COMMAND_BUFFER_LEVEL_PRIMARY : VK_COMMAND_BUFFER_LEVEL_SECONDARY)
                    .commandBufferCount(1);
            PointerBuffer pCommandBuffer = stack.mallocPointer(1);
            VulkanUtils.vkCheck(vkAllocateCommandBuffers(vkDevice, pCmdBufAllocateInfo, pCommandBuffer),
                    "Failed to allocate render command buffer");

            vkCommandBuffer = new VkCommandBuffer(pCommandBuffer.get(0), vkDevice);
        }
    }

    public void cleanup() {
        VK10.vkFreeCommandBuffers(commandPool.getDevice().getVkDevice(), commandPool.getVkCommandPool(),
                vkCommandBuffer);
    }

    public void beginRecording() throws OxygenException {
        beginRecording(null);
    }

    public void beginRecording(InheritanceInfo inheritanceInfo) throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferBeginInfo cmdBufInfo = VkCommandBufferBeginInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            if (oneTimeSubmit) {
                cmdBufInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            }
            if (!primary) {
                if (inheritanceInfo == null) {
                    throw new RuntimeException("Secondary buffers must declare inheritance info");
                }
                VkCommandBufferInheritanceInfo vkInheritanceInfo = VkCommandBufferInheritanceInfo.calloc(stack)
                        .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_INHERITANCE_INFO)
                        .renderPass(inheritanceInfo.vkRenderPass())
                        .subpass(inheritanceInfo.subPass())
                        .framebuffer(inheritanceInfo.vkFramebuffer());
                cmdBufInfo.pInheritanceInfo(vkInheritanceInfo);
                cmdBufInfo.flags(VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT);
            }
            VulkanUtils.vkCheck(vkBeginCommandBuffer(vkCommandBuffer, cmdBufInfo), "Failed to begin command buffer");
        }
    }

    public void endRecording() throws OxygenException {
        VulkanUtils.vkCheck(vkEndCommandBuffer(vkCommandBuffer), "Failed to end command buffer");
    }

    public VkCommandBuffer getVkCommandBuffer() {
        return vkCommandBuffer;
    }

    public void reset() {
        vkResetCommandBuffer(vkCommandBuffer, VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT);
    }

}
