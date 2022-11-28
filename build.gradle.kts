plugins {
    java
    `java-library`

    id("com.github.johnrengelman.shadow") version "7.0.0"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    tasks {
        compileJava  {
            sourceCompatibility = "17"
            targetCompatibility = "17"
        }
    }

    dependencies {
        implementation("org.apache.commons:commons-lang3:3.12.0")
        implementation("com.google.guava:guava:31.1-jre")
        implementation("ltd.lemongaming:configurate-yaml:4.2.0-SNAPSHOT")
        implementation("io.leangen.geantyref:geantyref:1.3.13")
        implementation("org.slf4j:slf4j-api:2.0.5")
        implementation("net.fabricmc:mapping-io:0.3.0")
        implementation("com.mojang:logging:1.1.1")

        compileOnly("net.kyori:adventure-api:4.11.0")
        compileOnly("net.kyori:adventure-text-minimessage:4.11.0")

        testImplementation("junit:junit:4.13.2")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
        testImplementation("org.assertj:assertj-core:3.23.1")
        testImplementation("net.kyori:adventure-api:4.11.0")
        testImplementation("net.kyori:adventure-text-minimessage:4.11.0")
    }

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            url = uri("https://papermc.io/repo/repository/maven-public/")
        }

        maven {
            url = uri("https://maven.pkg.github.com/LemonGamingLtd/configurate")
        }
    }
}

tasks {
    // auto relocation
    task<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
        target = shadowJar.get()
        prefix = providers.gradleProperty("mainPackage").get() + ".libs"
    }

    // fat jar
    shadowJar {
        dependsOn("relocateShadowJar")
        archiveClassifier.set("")
    }

    assemble {
        dependsOn("shadowJar")
    }
}