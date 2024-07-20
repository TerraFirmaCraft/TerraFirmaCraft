import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    id("net.neoforged.moddev") version "0.1.126"
    id("net.neoforged.licenser") version "0.7.2"
}


// Toolchain versions
val minecraftVersion: String = "1.21"
val neoForgeVersion: String = "21.0.113-beta"
val parchmentVersion: String = "2024.07.07"
val parchmentMinecraftVersion: String = "1.21"

// Dependency versions
val jeiVersion: String = "15.2.0.21"
val patchouliVersion: String = "1.20.1-81-FORGE"
val jadeVersion: String = "4614153"
val topVersion: String = "4629624"

val modId: String = "tfc"
val modVersion: String = System.getenv("VERSION") ?: "0.0.0-indev"
val modJavaVersion: String = "21"
val modIsInCI: Boolean = !modVersion.contains("-indev")
val modDataOutput: String = "src/generated/resources"


base {
    archivesName.set("TerraFirmaCraft-NeoForge-$minecraftVersion")
    group = "net.dries007.tfc"
    version = modVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://dvs1.progwml6.com/files/maven/") // JEI
    maven(url = "https://modmaven.k-4u.nl") // Mirror for JEI
    maven(url = "https://maven.blamejared.com") // Patchouli
    maven(url = "https://www.cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    // JEI
    //compileOnly(fg.deobf("mezz.jei:jei-$minecraftVersion-forge-api:$jeiVersion"))
    //compileOnly(fg.deobf("mezz.jei:jei-$minecraftVersion-common-api:$jeiVersion"))
    //runtimeOnly(fg.deobf("mezz.jei:jei-$minecraftVersion-forge:$jeiVersion"))

    // Patchouli
    // We need to compile against the full JAR, not just the API, because we do some egregious hacks.
    //compileOnly(fg.deobf("vazkii.patchouli:Patchouli:$patchouliVersion"))
    //runtimeOnly(fg.deobf("vazkii.patchouli:Patchouli:$patchouliVersion"))

    // Jade / The One Probe
    //compileOnly(fg.deobf("curse.maven:jade-324717:${jadeVersion}"))
    //compileOnly(fg.deobf("curse.maven:top-245211:${topVersion}"))

    // Only use Jade at runtime
    //runtimeOnly(fg.deobf("curse.maven:jade-324717:${jadeVersion}"))
    // runtimeOnly(fg.deobf("curse.maven:top-245211:${topVersion}"))

    // JUnit
    // There is not a testImplementation-like configuration, AFAIK, that is available at minecraft runtime, so we use minecraftLibrary
    //minecraftLibrary("org.junit.jupiter:junit-jupiter-api:5.9.2")
    //minecraftLibrary("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

sourceSets.main {
    resources {
        srcDir(modDataOutput)
    }
}
val sourceSetsDeprecated = sourceSets.create("deprecated") {} // A source set for compat/test code that is currently not functional
sourceSets {
    create("data") {
        runtimeClasspath += sourceSets.main.get().output
        compileClasspath += sourceSets.main.get().output
    }
}

neoForge {
    version.set(neoForgeVersion)
    addModdingDependenciesTo(sourceSets["data"])
    validateAccessTransformers = true

    parchment {
        minecraftVersion.set(parchmentMinecraftVersion)
        mappingsVersion.set(parchmentVersion)
    }

    runs {
        configureEach {
            // Only JBR allows enhanced class redefinition, so ignore the option for any other JDKs
            jvmArguments.addAll("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition", "-ea")
        }
        register("client") {
            client()
            gameDirectory = file("run/client")
        }
        register("server") {
            server()
            gameDirectory = file("run/server")
            programArgument("--nogui")
        }
        register("data") {
            data()
            sourceSet = sourceSets["data"]
            programArguments.addAll("--all", "--mod", modId, "--output", file(modDataOutput).absolutePath, "--existing",  file("src/main/resources").absolutePath)
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["data"])
        }
    }
}

// Automatically apply a license header when running checkLicense / updateLicense
license {
    header(project.file("HEADER.txt"))

    include("**/*.java")
    exclude("net/dries007/tfc/world/noise/FastNoiseLite.java") // Fast Noise
}

tasks {
    processResources {
        val modReplacementProperties = mapOf(
            "modId" to modId,
            "modVersion" to modVersion,
            "minecraftVersionRange" to "[$minecraftVersion,)",
            "neoForgeVersionRange" to "[$neoForgeVersion,)",
        )

        inputs.properties(modReplacementProperties)
        filesMatching(listOf("book.json", "META-INF/neoforge.mods.toml")) { expand(modReplacementProperties) }

        if (modIsInCI) {
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
        }
    }
}

