import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
}

intellij {
    pluginName = providers.gradleProperty("pluginName")
    type = providers.gradleProperty("platformType")
    version = providers.gradleProperty("platformVersion")

    // Depend on the Java plugin so we can access CompilerManager
    plugins = listOf("java")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild = "241"
        untilBuild = "251.*"
    }

    signPlugin {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishPlugin {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}
