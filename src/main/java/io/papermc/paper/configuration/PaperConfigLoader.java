package io.papermc.paper.configuration;

import static io.leangen.geantyref.GenericTypeReflector.erase;

import com.mojang.logging.LogUtils;
import io.papermc.paper.configuration.constraint.Constraint;
import io.papermc.paper.configuration.constraint.Constraints;
import io.papermc.paper.configuration.serializer.ComponentSerializer;
import io.papermc.paper.configuration.serializer.EnumValueSerializer;
import io.papermc.paper.configuration.serializer.collections.MapSerializer;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.CheckedFunction;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class PaperConfigLoader {

	private static final Logger LOGGER = LogUtils.getLogger();

	private YamlConfigurationLoader.Builder loaderBuilder;

	public PaperConfigLoader() {
		loaderBuilder = defaultLoader();
	}

	public PaperConfigLoader loader(final UnaryOperator<YamlConfigurationLoader.Builder> loaderBuilder) {
		this.loaderBuilder = loaderBuilder.apply(this.loaderBuilder);
		return this;
	}

	public PaperConfigLoader options(final UnaryOperator<ConfigurationOptions> options) {
		loaderBuilder.defaultOptions(options);
		return this;
	}

	public <T> T load(File configFile, Class<T> configClass) throws ConfigurateException {
		return initializeConfiguration(creator(configClass, true), configFile, loaderBuilder);
	}

	private static <T> T initializeConfiguration(final CheckedFunction<ConfigurationNode, T, SerializationException> creator,
			File configFile, YamlConfigurationLoader.Builder loaderBuilder) throws ConfigurateException {
		final Path configPath = configFile.toPath();
		final ConfigurationLoader<?> loader = loaderBuilder
				.path(configPath)
				.build();
		final ConfigurationNode node;
		if (Files.exists(configPath)) {
			node = loader.load();
		} else {
			node = CommentedConfigurationNode.root(loader.defaultOptions());
		}
		applyDefaultTransformations(node);
		final T instance = creator.apply(node);
		trySaveFileNode(loader, node, configPath.toString());
		return instance;
	}

	public static YamlConfigurationLoader.Builder defaultLoader() {
		return ConfigurationLoaders.naturallySorted()
				.commentsEnabled(true)
				.defaultOptions(options -> defaultOptions(options, createObjectMapper()));
	}

	public static ConfigurationOptions defaultOptions(ConfigurationOptions options, final ObjectMapper.Factory factory) {
		return options
				.serializers(builder -> builder
					.register(MapSerializer.TYPE, new MapSerializer(false))
					.register(new EnumValueSerializer())
					.register(new ComponentSerializer())
					.register(PaperConfigLoader::isConfigType, factory.asTypeSerializer())
					.registerAnnotatedObjects(factory)
				);
	}

	public static ObjectMapper.Factory createObjectMapper() {
		return ObjectMapper.factoryBuilder()
				.addDiscoverer(InnerClassFieldDiscoverer.defaultDiscoverer())
				.addConstraint(Constraint.class, new Constraint.Factory())
				.addConstraint(Constraints.Min.class, Number.class, new Constraints.Min.Factory())
				.build();
	}

	private static void applyDefaultTransformations(final ConfigurationNode node) throws ConfigurateException {
	}

	private static void trySaveFileNode(ConfigurationLoader<?> loader, ConfigurationNode node, String filename) throws ConfigurateException {
		try {
			loader.save(node);
		} catch (ConfigurateException ex) {
			if (ex.getCause() instanceof AccessDeniedException) {
				LOGGER.warn("Could not save {}: Paper could not persist the full set of configuration settings in the configuration file. Any setting missing from the configuration file will be set with its default value in memory. Admins should make sure to review the configuration documentation at https://docs.papermc.io/paper/configuration for more details.", filename, ex);
			} else throw ex;
		}
	}

	private static <T> CheckedFunction<ConfigurationNode, T, SerializationException> creator(Class<T> type, boolean refreshNode) {
		return node -> {
			T instance = node.require(type);
			if (refreshNode) {
				node.set(type, instance);
			}
			return instance;
		};
	}

	private static boolean isConfigType(final Type type) {
		return ConfigurationPart.class.isAssignableFrom(erase(type));
	}

}
