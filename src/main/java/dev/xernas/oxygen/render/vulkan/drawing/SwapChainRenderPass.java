package dev.xernas.oxygen.render.vulkan.drawing;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class SwapChainRenderPass implements IOxygenLogic {

    private final SwapChain swapchain;
    private long vkRenderPass;

    public SwapChainRenderPass(SwapChain swapchain) {
        this.swapchain = swapchain;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkAttachmentDescription.Buffer pAttachments = VkAttachmentDescription.calloc(1, stack);

            // Color attachment
            pAttachments.get(0)
                    .format(swapchain.getSurfaceFormat().format())
                    .samples(VK_SAMPLE_COUNT_1_BIT)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .finalLayout(KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

            VkAttachmentReference.Buffer pColorReference = VkAttachmentReference.calloc(1, stack)
                    .attachment(0)
                    .layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            VkSubpassDescription.Buffer pSubPass = VkSubpassDescription.calloc(1, stack)
                    .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                    .colorAttachmentCount(pColorReference.remaining())
                    .pColorAttachments(pColorReference);

            VkSubpassDependency.Buffer pSubpassDependencies = VkSubpassDependency.calloc(1, stack);
            pSubpassDependencies.get(0)
                    .srcSubpass(VK_SUBPASS_EXTERNAL)
                    .dstSubpass(0)
                    .srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .srcAccessMask(0)
                    .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);

            VkRenderPassCreateInfo pRenderPassInfo = VkRenderPassCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                    .pAttachments(pAttachments)
                    .pSubpasses(pSubPass)
                    .pDependencies(pSubpassDependencies);

            LongBuffer pRenderPass = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreateRenderPass(swapchain.getDevice().getVkDevice(), pRenderPassInfo, null, pRenderPass),
                    "Failed to create render pass");
            vkRenderPass = pRenderPass.get(0);
        }
    }

    public void cleanup() {
        vkDestroyRenderPass(swapchain.getDevice().getVkDevice(), vkRenderPass, null);
    }

    public long getVkRenderPass() {
        return vkRenderPass;
    }

}
