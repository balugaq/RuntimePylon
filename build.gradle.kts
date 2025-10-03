import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    idea
    id("com.gradleup.shadow") version "8.3.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("io.freefair.lombok") version "8.13.1"
    `maven-publish`
    signing
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.4"
}

group = "com.balugaq"

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        name = "sonatype"
    }
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.xenondevs.xyz/releases")
}

val coreVersion = project.properties["pylon-core.version"] as String
val baseVersion = project.properties["pylon-base.version"] as String


dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("io.github.pylonmc:pylon-core:$coreVersion")

    compileOnly("com.caoccao.javet:javet:4.1.7")
    compileOnly("com.caoccao.javet:javet-v8-linux-arm64:4.1.7")
    compileOnly("com.caoccao.javet:javet-v8-linux-x86_64:4.1.7")
    //compileOnly("com.caoccao.javet:javet-v8-macos-arm64:4.1.7")
    //compileOnly("com.caoccao.javet:javet-v8-macos-x86_64:4.1.7")
    compileOnly("com.caoccao.javet:javet-v8-windows-x86_64:4.1.7")
    //compileOnly("io.github.pylonmc:pylon-base:$baseVersion")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.shadowJar {
    mergeServiceFiles()

    fun doRelocate(lib: String) {
        relocate(lib, "com.balugaq.runtimepylon.shadowlibs.$lib")
    }

    archiveBaseName = project.name
    archiveClassifier = null
}

bukkit {
    name = "RuntimePylon"
    main = "com.balugaq.runtimepylon.RuntimePylon"
    version = project.version.toString()
    apiVersion = "1.21"
    depend = listOf("PylonCore")
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}

tasks.runServer {
    downloadPlugins {
        github("pylonmc", "pylon-core", coreVersion, "pylon-core-$coreVersion.jar")
        github("pylonmc", "pylon-base", baseVersion, "pylon-base-$baseVersion.jar")
    }
    maxHeapSize = "4G"
    minecraftVersion("1.21.4")
}

// Disable signing for maven local publish
if (project.gradle.startParameter.taskNames.any { it.contains("publishToMavenLocal") }) {
    tasks.withType<Sign>().configureEach {
        enabled = false
    }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

signing {
    useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASSWORD"))
}
