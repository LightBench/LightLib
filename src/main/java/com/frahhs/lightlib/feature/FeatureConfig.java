package com.frahhs.lightlib.feature;

import com.frahhs.lightlib.LightPlugin;
import com.frahhs.lightlib.config.ConfigEntry;
import com.frahhs.lightlib.config.LightConfig;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlFile;
import org.yaml.snakeyaml.comments.CommentType;

import java.util.List;

public class FeatureConfig {
    private final LightFeature feature;
    private final FeatureConfig parent;

    private final LightConfig lightConfig;

    public FeatureConfig(LightFeature feature, LightPlugin plugin, FeatureConfig parent) {
        this.feature = feature;
        this.parent = parent;

        this.lightConfig = plugin.getLightConfig();
    }


    public FeatureConfig(LightFeature feature, LightPlugin plugin) {
        this(feature, plugin, null);
    }

    public ConfigEntry addEntry(@NotNull String key, @NotNull Object defaultValue) {
        return lightConfig.add(generatePath(key), defaultValue);
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
}
