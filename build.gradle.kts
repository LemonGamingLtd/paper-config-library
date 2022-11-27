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
        compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    }

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            url = uri("https://papermc.io/repo/repository/maven-public/")
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