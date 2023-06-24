pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.minecraftforge.net/")
        maven(url = "https://repo.spongepowered.org/repository/maven-public/")
        maven(url = "https://maven.parchmentmc.org")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "org.spongepowered") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}