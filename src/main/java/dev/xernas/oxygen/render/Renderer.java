package dev.xernas.oxygen.render;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.*;
import dev.xernas.oxygen.render.vulkan.command.CommandPool;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.device.PhysicalDevice;
import dev.xernas.oxygen.render.vulkan.drawing.surface.Surface;
import dev.xernas.oxygen.render.vulkan.drawing.SwapChain;
import dev.xernas.oxygen.render.vulkan.command.GraphicsQueue;
import dev.xernas.oxygen.render.vulkan.command.PresentQueue;
import dev.xernas.oxygen.render.vulkan.model.Model;
import dev.xernas.oxygen.render.vulkan.model.ModelData;
import dev.xernas.oxygen.render.vulkan.pipeline.PipelineCache;

import java.util.ArrayList;
import java.util.List;

public class Renderer implements IOxygenLogic {

    private final List<Model> models;

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

    public Renderer(Oxygen oxygen) {
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

        this.models = new ArrayList<>();
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

    public void cleanup() throws OxygenException {
        presentQueue.waitIdle();
        graphicsQueue.waitIdle();
        device.waitIdle();

        for (Model model : models) {
            model.cleanup();
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

    public void loadModels(List<ModelData> modelDataList) throws OxygenException {
        models.addAll(Model.transformModels(modelDataList, commandPool, graphicsQueue));
    }


    public void render() throws OxygenException {
        forwardRenderActivity.waitForFence();
        swapChain.acquireNextImage();

        forwardRenderActivity.recordCommandBuffer(models);
        forwardRenderActivity.submit(graphicsQueue);

        swapChain.presentImage(presentQueue);
    }

}
