import ca.stellardrift.build.configurate.ConfigFormats
import ca.stellardrift.build.configurate.transformations.convertFormat
import net.fabricmc.loom.task.RunGameTask
import net.kyori.indra.repository.sonatypeSnapshots

plugins {
  val indraVersion = "2.1.1"
  id("fabric-loom") version "0.11-SNAPSHOT"
  id("io.github.juuxel.loom-quiltflower") version "1.6.0"
  id("ca.stellardrift.configurate-transformations") version "5.0.1"
  id("net.kyori.indra") version indraVersion
  id("net.kyori.indra.license-header") version indraVersion
  id("net.kyori.indra.checkstyle") version indraVersion
  id("net.kyori.indra.publishing.sonatype") version indraVersion
}

val versionAdventure: String by project
val versionAdventurePlatform: String by project
val versionColonel: String by project
val versionExamination: String by project
val versionFabricApi: String by project
val versionJetbrainsAnnotations: String by project
val versionLoader: String by project
val versionMinecraft: String by project
val versionParchment: String by project

group = "net.kyori"
version = "5.1.0-SNAPSHOT"
description = "Integration between the adventure library and Minecraft: Java Edition, using the Fabric modding system"

repositories {
  mavenCentral()
  sonatypeSnapshots()
  maven(url = "https://maven.parchmentmc.org/") {
    name = "parchment"
  }
}

quiltflower {
  quiltflowerVersion.set("1.7.0")
  preferences(
    "win" to 0
  )
  addToRuntimeClasspath.set(true)
}

indra {
  javaVersions().target(17)
}

license {
  header(file("LICENSE_HEADER"))
}

dependencies {
  annotationProcessor("ca.stellardrift:contract-validator:1.0.1")
  modApi(include("net.kyori:adventure-key:$versionAdventure")!!)
  modApi(include("net.kyori:adventure-api:$versionAdventure")!!)
  modApi(include("net.kyori:adventure-text-serializer-plain:$versionAdventure")!!)
  modApi(include("net.kyori:adventure-platform-api:$versionAdventurePlatform") {
    exclude("com.google.code.gson")
  })
  modImplementation(include("net.kyori:adventure-text-serializer-gson:$versionAdventure") {
    exclude("com.google.code.gson")
  })
  modApi(fabricApi.module("fabric-api-base", versionFabricApi))

  // Transitive deps
  include("net.kyori:examination-api:$versionExamination")
  include("net.kyori:examination-string:$versionExamination")
  modCompileOnly("org.jetbrains:annotations:$versionJetbrainsAnnotations")

  modImplementation("ca.stellardrift:colonel:$versionColonel")

  minecraft("com.mojang:minecraft:$versionMinecraft")
  mappings(loom.layered {
    officialMojangMappings()
    parchment("org.parchmentmc.data:parchment-$versionParchment@zip")
  })
  modImplementation("net.fabricmc:fabric-loader:$versionLoader")

  // Testmod TODO figure out own scope
  val api = "net.fabricmc.fabric-api:fabric-api:$versionFabricApi"
  if (gradle.startParameter.taskNames.contains("publish")) {
    modCompileOnly(api)
  } else {
    modImplementation(api)
  }

  checkstyle("ca.stellardrift:stylecheck:0.1")
}

// tasks.withType(net.fabricmc.loom.task.RunGameTask::class) {
//   setClasspath(files(loom.unmappedModCollection, sourceSets.main.map { it.runtimeClasspath }))
// }

sourceSets {
  main {
    java.srcDirs(
      "src/accessor/java",
      "src/mixin/java"
    )
    resources.srcDirs(
      "src/accessor/resources/",
      "src/mixin/resources/"
    )
  }
  register("testmod") {
    compileClasspath += main.get().compileClasspath
    runtimeClasspath += main.get().runtimeClasspath
    java.srcDirs("src/testmodMixin/java")
    resources.srcDirs("src/testmodMixin/resources")
  }
}

dependencies {
  "testmodImplementation"(sourceSets.main.map { it.output })
}

loom {
  runs {
    register("testmodClient") {
      source("testmod")
      client()
    }
    register("testmodServer") {
      source("testmod")
      server()
    }
  }
}

tasks.withType(RunGameTask::class) {
  javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(indra.javaVersions().target().map { v -> JavaLanguageVersion.of(v) })})
}

// Convert yaml files to josn
tasks.withType(ProcessResources::class.java).configureEach {
    inputs.property("version", project.version)

    // Convert data files yaml -> json
    filesMatching(
        sequenceOf(
            "fabric.mod",
            "data/**/*",
            "assets/**/*"
        ).flatMap { base -> sequenceOf("$base.yml", "$base.yaml") }
            .toList()
    ) {
        convertFormat(ConfigFormats.YAML, ConfigFormats.JSON)
        if (name.startsWith("fabric.mod")) {
            expand("project" to project)
        }
        name = name.substringBeforeLast('.') + ".json"
    }
    // Convert pack meta, without changing extension
    filesMatching("pack.mcmeta") { convertFormat(ConfigFormats.YAML, ConfigFormats.JSON) }
}

indra {
  github("KyoriPowered", "adventure-platform-fabric") {
    ci(true)
  }
  mitLicense()

  configurePublications {
    pom {
      developers {
        developer {
          id.set("kashike")
          timezone.set("America/Vancouver")
        }

        developer {
          id.set("lucko")
          name.set("Luck")
          url.set("https://lucko.me")
          email.set("git@lucko.me")
        }

        developer {
          id.set("zml")
          name.set("zml")
          timezone.set("America/Vancouver")
        }

        developer {
          id.set("Electroid")
        }
      }
    }
  }
}

// Workaround for both loom and indra doing publication logic in an afterEvaluate :(
indra.includeJavaSoftwareComponentInPublications(false)
publishing {
  publications.named("maven", MavenPublication::class) {
   from(components["java"])
  }
}
