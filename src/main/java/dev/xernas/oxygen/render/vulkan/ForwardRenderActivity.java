package dev.xernas.oxygen.render.vulkan;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.buffers.VertexBufferStructure;
import dev.xernas.oxygen.render.vulkan.command.CommandBuffer;
import dev.xernas.oxygen.render.vulkan.command.CommandPool;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.drawing.FrameBuffer;
import dev.xernas.oxygen.render.vulkan.drawing.ImageView;
import dev.xernas.oxygen.render.vulkan.drawing.SwapChain;
import dev.xernas.oxygen.render.vulkan.drawing.SwapChainRenderPass;
import dev.xernas.oxygen.render.vulkan.command.Queue;
import dev.xernas.oxygen.render.vulkan.model.VulkanModel;
import dev.xernas.oxygen.render.vulkan.pipeline.Pipeline;
import dev.xernas.oxygen.render.vulkan.pipeline.PipelineCache;
import dev.xernas.oxygen.render.vulkan.shader.VulkanShaderProgram;
import dev.xernas.oxygen.render.vulkan.sync.Fence;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

public class ForwardRenderActivity implements IOxygenLogic {

    private static final String FRAGMENT_SHADER_FILE_GLSL = "shaders/default/default.frag";
    private static final String VERTEX_SHADER_FILE_GLSL = "shaders/default/default.vert";

    private final SwapChain swapChain;
    private final CommandPool commandPool;
    private final PipelineCache pipelineCache;

    private SwapChainRenderPass renderPass;
    private FrameBuffer[] frameBuffers;
    private CommandBuffer[] commandBuffers;
    private Fence[] fences;
    private VulkanShaderProgram fwdShaderProgram;
    private Pipeline pipeline;

    public ForwardRenderActivity(SwapChain swapChain, CommandPool commandPool, PipelineCache pipelineCache) {
        this.swapChain = swapChain;
        this.commandPool = commandPool;
        this.pipelineCache = pipelineCache;
    }

    @Override
    public void init() throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            Device device = swapChain.getDevice();
            VkExtent2D swapChainExtent = swapChain.getSwapChainExtent();
            ImageView[] imageViews = swapChain.getImageViews();
            int numImages = imageViews.length;

            renderPass = new SwapChainRenderPass(swapChain);
            renderPass.init();

            LongBuffer pAttachments = stack.mallocLong(1);
            frameBuffers = new FrameBuffer[numImages];
            for (int i = 0; i < numImages; i++) {
                pAttachments.put(0, imageViews[i].getVkImageView());
                FrameBuffer newFrameBuffer = new FrameBuffer(device, swapChainExtent.width(), swapChainExtent.height(), pAttachments, renderPass.getVkRenderPass());
                newFrameBuffer.init();
                frameBuffers[i] = newFrameBuffer;
            }

            // Drawing

            fwdShaderProgram = new VulkanShaderProgram(device, new VulkanShaderProgram.ShaderModuleData[]
                    {
                            new VulkanShaderProgram.ShaderModuleData(VK_SHADER_STAGE_VERTEX_BIT, VERTEX_SHADER_FILE_GLSL),
                            new VulkanShaderProgram.ShaderModuleData(VK_SHADER_STAGE_FRAGMENT_BIT, FRAGMENT_SHADER_FILE_GLSL),
                    });
            fwdShaderProgram.init();
            VertexBufferStructure vertexBufferStructure = new VertexBufferStructure();
            vertexBufferStructure.init();
            Pipeline.PipeLineCreationInfo pipeLineCreationInfo = new Pipeline.PipeLineCreationInfo(
                    renderPass.getVkRenderPass(), fwdShaderProgram, 1, vertexBufferStructure);
            pipeline = new Pipeline(pipelineCache, pipeLineCreationInfo);
            pipeline.init();
            pipeLineCreationInfo.cleanup();

            commandBuffers = new CommandBuffer[numImages];
            fences = new Fence[numImages];
            for (int i = 0; i < numImages; i++) {
                CommandBuffer commandBuffer = new CommandBuffer(commandPool, true, false);
                commandBuffer.init();
                commandBuffers[i] = commandBuffer;
                Fence fence = new Fence(device, true);
                fence.init();
                fences[i] = fence;
            }

        }
    }

    public void cleanup() {
        Arrays.asList(frameBuffers).forEach(FrameBuffer::cleanup);
        renderPass.cleanup();
        Arrays.asList(commandBuffers).forEach(CommandBuffer::cleanup);
        Arrays.asList(fences).forEach(Fence::cleanup);
    }

    public void submit(Queue queue) throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int idx = swapChain.getCurrentFrame();
            CommandBuffer commandBuffer = commandBuffers[idx];
            Fence currentFence = fences[idx];
            currentFence.reset();
            SwapChain.SyncSemaphores syncSemaphores = swapChain.getSyncSemaphoresList()[idx];
            queue.submit(stack.pointers(commandBuffer.getVkCommandBuffer()),
                    stack.longs(syncSemaphores.imgAcquisitionSemaphore().getVkSemaphore()),
                    stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),
                    stack.longs(syncSemaphores.renderCompleteSemaphore().getVkSemaphore()), currentFence);
        }
    }

    public void recordCommandBuffer(List<VulkanModel> vulkanVulkanModelList) throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkExtent2D swapChainExtent = swapChain.getSwapChainExtent();
            int width = swapChainExtent.width();
            int height = swapChainExtent.height();
            int idx = swapChain.getCurrentFrame();

            CommandBuffer commandBuffer = commandBuffers[idx];
            FrameBuffer frameBuffer = frameBuffers[idx];

            commandBuffer.reset();
            VkClearValue.Buffer clearValues = VkClearValue.calloc(1, stack);
            clearValues.apply(0, v -> v.color().float32(0, 1f).float32(1, 0f).float32(2, 0f).float32(3, 1f));

            VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                    .renderPass(renderPass.getVkRenderPass())
                    .pClearValues(clearValues)
                    .renderArea(a -> a.extent().set(width, height))
                    .framebuffer(frameBuffer.getVkFrameBuffer());

            commandBuffer.beginRecording();
            VkCommandBuffer cmdHandle = commandBuffer.getVkCommandBuffer();
            vkCmdBeginRenderPass(cmdHandle, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);

            vkCmdBindPipeline(cmdHandle, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getVkPipeline());

            VkViewport.Buffer viewport = VkViewport.calloc(1, stack)
                    .x(0)
                    .y(height)
                    .height(-height)
                    .width(width)
                    .minDepth(0.0f)
                    .maxDepth(1.0f);
            vkCmdSetViewport(cmdHandle, 0, viewport);

            VkRect2D.Buffer scissor = VkRect2D.calloc(1, stack)
                    .extent(it -> it
                            .width(width)
                            .height(height))
                    .offset(it -> it
                            .x(0)
                            .y(0));
            vkCmdSetScissor(cmdHandle, 0, scissor);

            LongBuffer offsets = stack.mallocLong(1);
            offsets.put(0, 0L);
            LongBuffer vertexBuffer = stack.mallocLong(1);
            for (VulkanModel vulkanModel : vulkanVulkanModelList) {
                for (VulkanModel.Mesh mesh : vulkanModel.getVulkanMeshList()) {
                    vertexBuffer.put(0, mesh.verticesBuffer().getBuffer());
                    vkCmdBindVertexBuffers(cmdHandle, 0, vertexBuffer, offsets);
                    vkCmdBindIndexBuffer(cmdHandle, mesh.indicesBuffer().getBuffer(), 0, VK_INDEX_TYPE_UINT32);
                    vkCmdDrawIndexed(cmdHandle, mesh.numIndices(), 1, 0, 0, 0);
                }
            }

            vkCmdEndRenderPass(cmdHandle);
            commandBuffer.endRecording();
        }
    }

    public void waitForFence() {
        int idx = swapChain.getCurrentFrame();
        Fence currentFence = fences[idx];
        currentFence.fenceWait();
    }

}
