import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    java
    idea
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("net.minecraftforge.gradle") version "5.1.+"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("org.spongepowered.mixin") version "0.7.+"
}

// Allows local configuration for a dev environment that importantly, isn't present on a build server.
// Edit the dev.gradle.kts file and define any of the below properties which use project.findProperty() under extra.apply { }, e.g.
//
// extra.apply {
//     set("minify_resources", false)
// }
// Properties that can be configured this way are:
// - "minify_resources" (to save time in dev)
// - "mappings_channel", "mappings_version" (for better mappings in dev)
// - "use_advanced_class_redefinition" (if using the Jetbrains Runtime JDK and want to enable -XX:+AllowEnhancedClassRedefinition for super amazing hotswap)
File("./dev.gradle.kts").createNewFile()
apply(from = "dev.gradle.kts")

// Toolchain versions
val minecraftVersion: String = "1.18.2"
val forgeVersion: String = "40.1.73"
val mixinVersion: String = "0.8.5"

// Dependency versions
val jeiVersion: String = "9.7.1.232"
val patchouliVersion: String = "1.18.2-70"
val jadeVersion: String = "3970956"
val topVersion: String = "3965688"

val modId: String = "tfc"
val modVersion: String = System.getenv("VERSION") ?: "0.0.0-indev"

// Optional dev-env properties
val mappingsChannel: String = project.findProperty("mappings_channel") as String? ?: "official"
val mappingsVersion: String = project.findProperty("mappings_version") as String? ?: minecraftVersion
val minifyResources: Boolean = project.findProperty("minify_resources") as Boolean? ?: false
val zipResources: Boolean = project.findProperty("zip_resources") as Boolean? ?: false
val useAdvancedClassRedef: Boolean = project.findProperty("use_advanced_class_redefinition") as Boolean? ?: false

println("Using mappings $mappingsChannel / $mappingsVersion with version $modVersion")

base {
    archivesName.set("TerraFirmaCraft-Forge-$minecraftVersion")
    group = "net.dries007.tfc"
    version = modVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://dvs1.progwml6.com/files/maven/") // JEI
    maven(url = "https://modmaven.k-4u.nl") // Mirror for JEI
    maven(url = "https://alcatrazescapee.jfrog.io/artifactory/mods") // Cyanide
    maven(url = "https://maven.blamejared.com") // Patchouli
    maven(url = "https://www.cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
    flatDir {
        dirs("libs")
    }
}

dependencies {
    minecraft("net.minecraftforge", "forge", version = "$minecraftVersion-$forgeVersion")

    // JEI
    compileOnly(fg.deobf("mezz.jei:jei-$minecraftVersion:$jeiVersion:api"))
    runtimeOnly(fg.deobf("mezz.jei:jei-$minecraftVersion:$jeiVersion"))

    // Patchouli
    // We need to compile against the full JAR, not just the API, because we do some egregious hacks.
    compileOnly(fg.deobf("vazkii.patchouli:Patchouli:$patchouliVersion"))
    runtimeOnly(fg.deobf("vazkii.patchouli:Patchouli:$patchouliVersion"))

    // Jade / The One Probe
    compileOnly(fg.deobf("curse.maven:jade-324717:${jadeVersion}"))
    compileOnly(fg.deobf("curse.maven:top-245211:${topVersion}"))

    // Only use Jade at runtime
    runtimeOnly(fg.deobf("curse.maven:jade-324717:${jadeVersion}"))
    // runtimeOnly(fg.deobf("curse.maven:top-245211:${topVersion}"))

    if (System.getProperty("idea.sync.active") != "true") {
        annotationProcessor("org.spongepowered:mixin:${mixinVersion}:processor")
    }

    // JUnit
    // There is not a testImplementation-like configuration, AFAIK, that is available at minecraft runtime, so we use minecraftLibrary
    minecraftLibrary("org.junit.jupiter:junit-jupiter-api:5.9.0")
    minecraftLibrary("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

minecraft {
    mappings(mappingsChannel, mappingsVersion)
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        all {
            args("-mixin.config=$modId.mixins.json")

            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", modId)

            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")

            jvmArgs("-ea", "-Xmx4G", "-Xms4G")

            if (useAdvancedClassRedef) {
                jvmArg("-XX:+AllowEnhancedClassRedefinition")
            }

            ideaModule("${project.name}.test")

            mods.create(modId) {
                source(sourceSets.main.get())
                source(sourceSets.test.get())
            }
        }

        register("client") {
            workingDirectory(project.file("run/client"))
            if (zipResources) {
                property("tfc.zippedResources", "true")
            }
        }

        register("server") {
            workingDirectory(project.file("run/server"))

            arg("--nogui")
        }

        register("gameTestServer") {
            workingDirectory(project.file("run/gametest"))

            arg("--nogui")
            forceExit(false)
        }
    }
}

// Automatically apply a license header when running checkLicense / updateLicense
license {
    header(project.file("HEADER.txt"))

    include("**/*.java")
    exclude("net/dries007/tfc/world/noise/FastNoiseLite.java") // Fast Noise
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    add(sourceSets.test.get(), "$modId.refmap.json")
}

curseforge {
    apiKey = System.getenv("CURSEFORGE_API_KEY") ?: "nope"

    project(closureOf<CurseProject> {
        id = "302973"
        changelog = "[Changelog ${version}](https://github.com/TerraFirmaCraft/TerraFirmaCraft/releases/tag/v${project.version})"
        changelogType = "markdown"

        releaseType = if (project.version.toString().contains("beta")) "beta" else if (project.version.toString().contains("alpha")) "alpha" else "release"
        addGameVersion(minecraftVersion)

        mainArtifact(tasks.jar.get())

        relations(closureOf<CurseRelation> {
            requiredDependency("patchouli")
            optionalDependency("jei")
        })
    })
}

tasks {

    processResources {

        if (modVersion != "0.0.0-indev") {
            filesMatching("**/book.json") {
                expand(mapOf("version" to project.version))
            }
        }

        if (zipResources) {
            doLast {
                val zipStart: Long = System.currentTimeMillis()
                delete("$projectDir/out/production/resources/assets/")
                delete("$projectDir/out/production/resources/data/tfc/advancements")
                delete("$projectDir/out/production/resources/data/tfc/loot_tables")
                delete("$projectDir/out/production/resources/data/tfc/recipes")
                delete("$projectDir/out/production/resources/data/tfc/tags")
                delete("$projectDir/out/production/resources/data/tfc/tfc")
                delete("$projectDir/out/production/resources/data/tfc/worldgen")
                delete("$projectDir/out/production/resources/data/minecraft")
                println("Deleted assets and data files in favor of zipped version. Took ${System.currentTimeMillis() - zipStart} ms")
            }
        }
        if (minifyResources) {
            doLast {
                val jsonMinifyStart: Long = System.currentTimeMillis()
                var jsonMinified: Long = 0
                var jsonBytesBefore: Long = 0
                var jsonBytesAfter: Long = 0

                fileTree(mapOf("dir" to outputs.files.asPath, "include" to "**/*.json")).forEach {
                    jsonMinified++
                    jsonBytesBefore += it.length()
                    try {
                        it.writeText(JsonOutput.toJson(JsonSlurper().parse(it)).replace("\"__comment__\":\"This file was automatically created by mcresources\",", ""))
                    } catch (e: Exception) {
                        println("JSON Error in ${it.path}")
                        throw e
                    }

                    jsonBytesAfter += it.length()
                }
                println("Minified $jsonMinified json files. Reduced ${jsonBytesBefore / 1024} kB to ${(jsonBytesAfter / 1024)} kB. Took ${System.currentTimeMillis() - jsonMinifyStart} ms")
            }
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("failed")

            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
            attributes["MixinConfigs"] = "$modId.mixins.json"
        }
    }

    register("generateSources", Copy::class) {
        from("src/main/java/")
        into("${buildDir}/generated-src")
        filesMatching("**/TerraFirmaCraft.java") {
            expand(mapOf("version" to project.version))
        }
    }

    compileJava {
        setSource("${buildDir}/generated-src")
        dependsOn("generateSources")
    }
}

