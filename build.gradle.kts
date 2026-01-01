@file:Suppress("VulnerableLibrariesLocal")

import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    idea
    id("com.gradleup.shadow") version "9.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("io.freefair.lombok") version "8.13.1"
}

group = project.properties["group"]!!

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        name = "sonatype"
    }
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://jitpack.io") {
        name = "JitPack"
    }
    maven("https://repo.xenondevs.xyz/releases") {
        name = "invui"
    }
    maven("https://repo.codemc.org/repository/maven-public/") {
        name = "codemc"
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        name = "placeholderapi"
    }
    maven("https://repo.minebench.de/") {
        name = "minebench"
    }
    maven("https://repo.alessiodp.com/releases/") {
        name = "alessiodp"
    }
}

val coreVersion = project.properties["pylon-core.version"] as String
val baseVersion = project.properties["pylon-base.version"] as String

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    compileOnly("io.github.pylonmc:pylon-core:$coreVersion")
    compileOnly("io.github.pylonmc:pylon-base:${baseVersion}")
    implementation("net.byteflux:libby-bukkit:1.3.1")
    compileOnly("com.caoccao.javet:javet:5.0.2")
    compileOnly("com.caoccao.javet:javet-node-linux-arm64:5.0.2")
    compileOnly("com.caoccao.javet:javet-node-linux-x86_64:5.0.2")
    compileOnly("com.caoccao.javet:javet-node-macos-arm64:5.0.2")
    compileOnly("com.caoccao.javet:javet-node-macos-x86_64:5.0.2")
    compileOnly("com.caoccao.javet:javet-node-windows-x86_64:5.0.2")
    compileOnly("com.caoccao.javet:javet-v8-linux-arm64:5.0.2")
    compileOnly("com.caoccao.javet:javet-v8-linux-x86_64:5.0.2")
    compileOnly("com.caoccao.javet:javet-v8-macos-arm64:5.0.2")
    compileOnly("com.caoccao.javet:javet-v8-macos-x86_64:5.0.2")
    compileOnly("com.caoccao.javet:javet-v8-windows-x86_64:5.0.2")
    compileOnly("org.apache.httpcomponents:httpclient:4.5.14")
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

    exclude("kotlin/**")
    exclude("org/intellij/lang/annotations/**")
    exclude("org/jetbrains/annotations/**")

    relocate("net.byteflux.libby", "${project.group}.${project.name}.libraries.net.byteflux.libby")

    archiveBaseName = project.name
    archiveClassifier = null
}

bukkit {
    name = project.properties["name"] as String
    main = project.properties["main-class"] as String
    version = project.version.toString()
    apiVersion = "1.21"
    depend = listOf("PylonCore")
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    softDepend = listOf(
        "PylonBase"
    )
}

tasks.runServer {
    downloadPlugins {
        github("pylonmc", "pylon-core", coreVersion, "pylon-core-$coreVersion.jar")
        github("pylonmc", "pylon-base", baseVersion, "pylon-base-$baseVersion.jar")
    }
    maxHeapSize = "4G"
    minecraftVersion("1.21.10")
}
