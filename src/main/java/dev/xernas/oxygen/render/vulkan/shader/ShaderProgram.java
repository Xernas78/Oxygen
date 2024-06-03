package dev.xernas.oxygen.render.vulkan.shader;

import dev.xernas.oxygen.IOxygenLogic;
import dev.xernas.oxygen.exception.OxygenException;
import dev.xernas.oxygen.render.vulkan.device.Device;
import dev.xernas.oxygen.render.vulkan.utils.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.file.Files;

import static org.lwjgl.vulkan.VK10.*;

public class ShaderProgram implements IOxygenLogic {

    private final Device device;
    private final ShaderModuleData[] shaderModuleData;
    private final ShaderModule[] shaderModules;
    private final int numModules;

    public ShaderProgram(Device device, ShaderModuleData[] shaderModuleData) {
        this.device = device;
        this.shaderModuleData = shaderModuleData;
        numModules = shaderModuleData != null ? shaderModuleData.length : 0;
        shaderModules = new ShaderModule[numModules];
    }

    @Override
    public void init() throws OxygenException {
        try {
            for (int i = 0; i < numModules; i++) {
                byte[] moduleContents = Files.readAllBytes(new File(shaderModuleData[i].shaderFile()).toPath());
                long moduleHandle = createShaderModule(moduleContents);
                shaderModules[i] = new ShaderModule(shaderModuleData[i].shaderStage(), moduleHandle);
            }
        } catch (IOException e) {
            throw new OxygenException(e.getMessage());
        }
    }

    public void cleanup() {
        for (ShaderModule shaderModule : shaderModules) {
            vkDestroyShaderModule(device.getVkDevice(), shaderModule.handle(), null);
        }
    }

    private long createShaderModule(byte[] code) throws OxygenException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer pCode = stack.malloc(code.length).put(0, code);

            VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                    .pCode(pCode);

            LongBuffer lp = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreateShaderModule(device.getVkDevice(), moduleCreateInfo, null, lp),
                    "Failed to create shader module");

            return lp.get(0);
        }
    }

    public static void compileShaderIfChanged(String glsShaderFile, int shaderType) {
        byte[] compiledShader;
        try {
            File glslFile = new File(glsShaderFile);
            File spvFile = new File(glsShaderFile + ".spv");
            if (!spvFile.exists() || glslFile.lastModified() > spvFile.lastModified()) {
                String shaderCode = new String(Files.readAllBytes(glslFile.toPath()));

                compiledShader = compileShader(shaderCode, shaderType);
                Files.write(spvFile.toPath(), compiledShader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] compileShader(String shaderCode, int shaderType) {
        long compiler = 0;
        long options = 0;
        byte[] compiledShader;

        try {
            compiler = Shaderc.shaderc_compiler_initialize();
            options = Shaderc.shaderc_compile_options_initialize();

            long result = Shaderc.shaderc_compile_into_spv(
                    compiler,
                    shaderCode,
                    shaderType,
                    "shader.glsl",
                    "main",
                    options
            );

            if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
                throw new RuntimeException("Shader compilation failed: " + Shaderc.shaderc_result_get_error_message(result));
            }

            ByteBuffer buffer = Shaderc.shaderc_result_get_bytes(result);
            compiledShader = new byte[buffer.remaining()];
            buffer.get(compiledShader);
        } finally {
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_compiler_release(compiler);
        }

        return compiledShader;
    }

    public ShaderModule[] getShaderModules() {
        return shaderModules;
    }

    public record ShaderModule(int shaderStage, long handle) {
    }

    public record ShaderModuleData(int shaderStage, String shaderFile) {
    }
}
