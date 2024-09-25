import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    id("net.neoforged.moddev") version "2.0.1-beta"
    id("net.neoforged.licenser") version "0.7.2"
}


// Toolchain versions
val minecraftVersion: String = "1.21"
val neoForgeVersion: String = "21.0.167"
val parchmentVersion: String = "2024.07.07"
val parchmentMinecraftVersion: String = "1.21"

// Dependency versions
val emiVersion: String = "1.1.10+1.21"
val jeiVersion: String = "19.5.2.66"
val patchouliVersion: String = "1.21-87-NEOFORGE-SNAPSHOT"
val jadeVersion: String = "5529595"
val topVersion: String = "1.21_neo-12.0.3-5"

val modId: String = "tfc"
val modVersion: String = System.getenv("VERSION") ?: "0.0.0-indev"
val modJavaVersion: String = "21"
val modIsInCI: Boolean = !modVersion.contains("-indev")
val modDataOutput: String = "src/generated/resources"


val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modReplacementProperties = mapOf(
        "modId" to modId,
        "modVersion" to modVersion,
        "minecraftVersionRange" to "[$minecraftVersion,)",
        "neoForgeVersionRange" to "[$neoForgeVersion,)",
        "jeiVersionRange" to "[$jeiVersion,)"
    )
    inputs.properties(modReplacementProperties)
    expand(modReplacementProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}


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
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com/") }
        filter { includeGroup("dev.emi") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.blamejared.com/") }
        filter { includeGroup("mezz.jei") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.k-4u.nl/") }
        filter { includeGroup("mcjty.theoneprobe") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.blamejared.com") }
        filter { includeGroup("vazkii.patchouli") }
    }
    exclusiveContent {
        forRepository { maven("https://www.cursemaven.com") }
        filter { includeGroup("curse.maven") }
    }
}

sourceSets {
    main {
        resources {
            srcDir(modDataOutput)
            srcDir(generateModMetadata)
        }
    }
    create("data")
}

dependencies {
    // EMI
    compileOnly("dev.emi:emi-neoforge:${emiVersion}:api")
    //runtimeOnly("dev.emi:emi-neoforge:${emiVersion}")

    // JEI
    compileOnly("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}")
    compileOnly("mezz.jei:jei-${minecraftVersion}-neoforge-api:${jeiVersion}")
    runtimeOnly("mezz.jei:jei-${minecraftVersion}-neoforge:${jeiVersion}")

    // Patchouli
    // We need to compile against the full JAR, not just the API, because we do some egregious hacks.
    implementation("vazkii.patchouli:Patchouli:$patchouliVersion")

    // Jade / The One Probe
    implementation("curse.maven:jade-324717:$jadeVersion")
    compileOnly("mcjty.theoneprobe:theoneprobe:$topVersion")

    // Data
    "dataImplementation"(sourceSets["main"].output)

    // Test
    // Use JUnit at runtime, plus depend on data to allow us to mock certain data without having to load a server
    testImplementation(sourceSets["data"].output)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
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
            systemProperty("tfc.enableDebugSelfTests", "true")
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

    unitTest {
        enable()
        testedMod = mods[modId];
    }

    ideSyncTask(generateModMetadata)
}

// Automatically apply a license header when running checkLicense / updateLicense
license {
    header(project.file("HEADER.txt"))

    include("**/*.java")
    exclude("net/dries007/tfc/world/noise/FastNoiseLite.java") // Fast Noise
}

tasks {
    processResources {
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
    }

    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
        }
    }

    named("neoForgeIdeSync") {
        dependsOn(generateModMetadata)
    }
}

