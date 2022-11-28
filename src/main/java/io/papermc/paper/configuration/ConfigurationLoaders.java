package io.papermc.paper.configuration;

import java.nio.file.Path;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.util.MapFactories;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public final class ConfigurationLoaders {
    private ConfigurationLoaders() {
    }

    public static YamlConfigurationLoader.Builder naturallySorted() {
        return YamlConfigurationLoader.builder()
            .headerMode(HeaderMode.PRESET)
            .defaultOptions(options -> options.mapFactory(MapFactories.sortedNatural()));
    }

    public static YamlConfigurationLoader naturallySortedWithoutHeader(final Path path) {
        return naturallySorted()
            .headerMode(HeaderMode.NONE)
            .path(path)
            .build();
    }
}
