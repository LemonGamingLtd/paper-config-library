package io.papermc.paper.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import io.papermc.paper.configuration.constraint.Constraints.Min;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@SuppressWarnings({"CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal", "NotNullFieldNotInitialized", "InnerClassMayBeStatic"})
public class TestConfig extends ConfigurationPart {

	@Test
	public void testLoadExisting() throws IOException {
		File file = new File("src/test/resources/testConfig.yml");
		TestConfig config = PaperConfig.createConfiguration(file, TestConfig.class);
		assertThat(config.testSection.testName).isEqualTo("nameOverride");
	}

	@Test
	public void testCreateNew() throws IOException {
		File file = File.createTempFile( "testConfigCreate", "yml");
		file.deleteOnExit();
		TestConfig config = PaperConfig.createConfiguration(file, TestConfig.class);
	}

	@Comment("Test comment")
	public TestSection testSection;

	public class TestSection extends ConfigurationPart {

		@Comment("Test comment")
		public String testName = "defaultName";

		@Min(10)
		public int testVal = 10;

	}

}
