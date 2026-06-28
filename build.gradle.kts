import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    id("net.neoforged.moddev") version "2.0.107"
    id("net.neoforged.licenser") version "0.7.2"
}


// Toolchain versions
val minecraftVersion: String = "1.21.1"
val neoForgeVersion: String = "21.1.197"
val parchmentVersion: String = "2024.11.17"
val parchmentMinecraftVersion: String = "1.21.1"

// Dependency versions
val emiVersion: String = "1.1.22+1.21.1"
val jeiVersion: String = "19.25.0.321"
val patchouliVersion: String = "1.21.1-92-NEOFORGE"

val modId: String = "tfc"
val modVersion: String = System.getenv("VERSION") ?: "0.0.0-indev"
val modJavaVersion: String = "21"
val modIsInCI: Boolean = !modVersion.contains("-indev")
val modDataOutput: String = "src/generated/resources"


val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modReplacementProperties = mapOf(
        "modId" to modId,
        "modVersion" to modVersion,
        "minecraftVersionRange" to "[$minecraftVersion]",
        "neoForgeVersionRange" to "[$neoForgeVersion,)",
        "patchouliVersionRange" to "[$patchouliVersion,)",
        "jeiVersionRange" to "[$jeiVersion,)"
    )
    inputs.properties(modReplacementProperties)
    expand(modReplacementProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

neoForge {
    version = neoForgeVersion // this is here because declaring a neoForge version enables 'additionalRuntimeClasspath'
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

neoForge {
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
    "dataImplementation"("vazkii.patchouli:Patchouli:$patchouliVersion")

    // Jade / The One Probe
    implementation(group = "curse.maven", name = "jade-324717", version = "6853386")
    compileOnly(group = "mcjty.theoneprobe", name = "theoneprobe", version = "1.21_neo-12.0.4-6")

    // ModernFix - useful at runtime for significant memory savings in TFC in dev (see i.e. wall block shape caches)
    runtimeOnly(group = "curse.maven", name = "modernfix-790626", version = "6766126")

    // Sodium - useful for testing graphics behavior
//    runtimeOnly(group = "curse.maven", name = "sodium-394468", version = "6382651")

    // Data
    "dataImplementation"(sourceSets["main"].output)

    // Test
    // Use JUnit at runtime, plus depend on data to allow us to mock certain data without having to load a server
    testImplementation(sourceSets["data"].output)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")
}

// Automatically apply a license header when running checkLicense / updateLicense
license {
    header(project.file("HEADER.txt"))

    include("**/*.java")
    exclude("net/dries007/tfc/world/noise/FastNoiseLite.java") // Fast Noise
}

abstract class MinifyJsonTask : DefaultTask() {

    @get:InputDirectory
    abstract val dir: DirectoryProperty

    @TaskAction
    fun minify() {
        val jsonSlurper = JsonSlurper()
        var jsonMinified = 0
        var jsonBytesBefore = 0L
        var jsonBytesAfter = 0L
        val start = System.currentTimeMillis()

        dir.get().asFileTree.matching {
            include("**/*.json")
        }.forEach { file ->
            jsonMinified++
            jsonBytesBefore += file.length()
            try {
                val parsed = jsonSlurper.parse(file)
                val minified = JsonOutput.toJson(parsed)
                    .replace("\"__comment__\":\"This file was automatically created by mcresources\",", "")
                file.writeText(minified)
            } catch (e: Exception) {
                logger.error("JSON Error in ${file.path}", e)
                throw e
            }
            jsonBytesAfter += file.length()
        }

        logger.lifecycle(
            "Minified $jsonMinified JSON files. Reduced ${jsonBytesBefore / 1024} kB → ${(jsonBytesAfter / 1024)} kB. Took ${System.currentTimeMillis() - start} ms"
        )
    }
}

if (modIsInCI) {
    tasks.register<MinifyJsonTask>("minifyJson") {
        // `processResources` writes into build/resources/<sourceSet>
        dir.set(layout.buildDirectory.dir("resources/main"))
    }

    tasks.named<ProcessResources>("processResources") {
        finalizedBy("minifyJson") // run AFTER processResources
    }
}


tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("failed", "standardError")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showCauses = true
            showExceptions = true
            showStackTraces = true
        }
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

