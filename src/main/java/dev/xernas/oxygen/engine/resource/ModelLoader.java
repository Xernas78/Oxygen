package dev.xernas.oxygen.engine.resource;

import dev.xernas.oxygen.Oxygen;
import dev.xernas.oxygen.engine.resource.loaders.OBJLoader;
import dev.xernas.oxygen.exception.OxygenException;

public class ModelLoader {

    public static IFormat loadFromResources(ResourceManager resourceManager, String resource, boolean parameter) {
        String extension = resource.substring(resource.lastIndexOf('.') + 1);
        return getLoader(resourceManager, extension).loadFromResources(resource, parameter);
    }

    private static ILoader getLoader(ResourceManager resourceManager, String extension) {
        switch (extension) {
            case "obj":
                return new OBJLoader(resourceManager);
            default:
                throw new UnsupportedOperationException("Unsupported format: " + extension);
        }
    }

}
