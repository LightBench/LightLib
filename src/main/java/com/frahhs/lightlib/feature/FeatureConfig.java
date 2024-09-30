package com.frahhs.lightlib.feature;

import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.feature.annotations.Feature;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.file.YamlFileWrapper;

import java.util.Objects;

public class FeatureConfig {
    private final LightFeature feature;
    private final FeatureConfig parent;

    private final YamlFile config;

    public FeatureConfig(LightFeature feature, LightPlugin plugin, FeatureConfig parent) {
        this.feature = feature;
        this.parent = parent;

        this.config = plugin.getLightConfig();
        this.commentProcessor();
    }

    public FeatureConfig(LightFeature feature, LightPlugin plugin) {
        this(feature, plugin, null);
    }

    @NotNull
    public YamlFileWrapper addEntry(@NotNull String key, @NotNull Object defaultValue) {
        // Add parameter
        config.addDefault(generatePath(key), defaultValue);

        return new YamlFileWrapper(config, generatePath(key));
    }

    @NotNull
    private String generatePath(@NotNull String key) {
        return String.format("%s.%s", getPath(), key);
    }

    @NotNull
    private String getPath() {
        if(parent == null)
            return feature.getID();
        else
            return String.format("%s.%s", parent.getPath(), feature.getID());
    }

    private void commentProcessor() {
        Class<? extends LightFeature> featureClass = feature.getClass();
        String configComment = "";

        // If key is a root key, generate the default description
        if(parent == null)
            configComment = String.format("%s feature configuration", feature.getID());

        // If the current Feature class is annotated and the description is not empty, retrieve the correct description.
        if(featureClass.isAnnotationPresent(Feature.class)) {
            Feature featureAnnotation = featureClass.getAnnotation(Feature.class);

            // Check if the Comment description is empty
            if(!Objects.equals(featureAnnotation.configComment(), "")) {
                configComment = featureAnnotation.configComment();
            }
        }

        // If the retrieved description is not void, generate it in the config.
        if(!Objects.equals(configComment, "")) {
            config.path(feature.getID()).comment(configComment);
        }
    }
}
