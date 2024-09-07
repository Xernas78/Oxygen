package dev.xernas.oxygen.render.vulkan;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.SceneEntity;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.IRenderer;
import dev.xernas.oxygen.engine.behaviors.ModelRenderer;
import dev.xernas.oxygen.render.vulkan.command.CommandPool;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.device.PhysicalDevice;
import dev.xernas.oxygen.render.vulkan.drawing.surface.Surface;
import dev.xernas.oxygen.render.vulkan.drawing.SwapChain;
import dev.xernas.oxygen.render.vulkan.command.GraphicsQueue;
import dev.xernas.oxygen.render.vulkan.command.PresentQueue;
import dev.xernas.oxygen.render.vulkan.model.VulkanModel;
import dev.xernas.oxygen.render.vulkan.pipeline.PipelineCache;

import java.util.ArrayList;
import java.util.List;

public class VulkanRenderer implements IRenderer {

    private final List<VulkanModel> vulkanModels;

    private final Instance instance;
    private final CommandPool commandPool;
    private final GraphicsQueue graphicsQueue;
    private final PresentQueue presentQueue;
    private final Device device;
    private final PhysicalDevice physicalDevice;
    private final Surface surface;
    private final SwapChain swapChain;
    private final PipelineCache pipelineCache;
    private final ForwardRenderActivity forwardRenderActivity;

    public VulkanRenderer(Oxygen oxygen) {
        this.instance = new Instance(oxygen.getApplicationName(), oxygen.getVersion(), oxygen.debugEnabled());
        this.physicalDevice = new PhysicalDevice(instance);
        this.device = new Device(physicalDevice);
        this.surface = new Surface(instance, oxygen.getWindow());
        this.graphicsQueue = new GraphicsQueue(device, 0);
        this.presentQueue = new PresentQueue(device, surface, 0);
        this.swapChain = new SwapChain(device, surface, oxygen.getWindow(), 2, oxygen.isVsync(), presentQueue, new GraphicsQueue[]{graphicsQueue});
        this.commandPool = new CommandPool(device, graphicsQueue.getQueueFamilyIndex());
        this.pipelineCache = new PipelineCache(device);
        this.forwardRenderActivity = new ForwardRenderActivity(swapChain, commandPool, pipelineCache);

        this.vulkanModels = new ArrayList<>();
    }

    @Override
    public void render() throws OxygenException {
        forwardRenderActivity.waitForFence();
        swapChain.acquireNextImage();

        forwardRenderActivity.recordCommandBuffer(vulkanModels);
        forwardRenderActivity.submit(graphicsQueue);

        swapChain.presentImage(presentQueue);
    }

    @Override
    public void init() throws OxygenException {
        instance.init();
        physicalDevice.init();
        device.init();
        surface.init();
        graphicsQueue.init();
        presentQueue.init();
        swapChain.init();
        commandPool.init();
        pipelineCache.init();
        forwardRenderActivity.init();
    }

    @Override
    public void cleanup() throws OxygenException {
        presentQueue.waitIdle();
        graphicsQueue.waitIdle();
        device.waitIdle();

        for (VulkanModel vulkanModel : vulkanModels) {
            vulkanModel.cleanup();
        }

        pipelineCache.cleanup();
        forwardRenderActivity.cleanup();
        commandPool.cleanup();
        swapChain.cleanup();
        surface.cleanup();
        device.cleanup();
        physicalDevice.cleanup();
        instance.cleanup();
    }

    @Override
    public void loadSceneEntities(List<SceneEntity> sceneEntities) throws OxygenException {
        for (SceneEntity sceneEntity : sceneEntities) {
            loadSceneEntity(sceneEntity);
        }
    }

    @Override
    public void loadSceneEntity(SceneEntity sceneEntity) throws OxygenException {
        ModelRenderer modelRenderer = sceneEntity.getBehavior(ModelRenderer.class);
        if (modelRenderer != null) vulkanModels.add(VulkanModel.transformModel(modelRenderer.getModelData(), commandPool, graphicsQueue));
    }

    @Override
    public void clear() {

    }

}
