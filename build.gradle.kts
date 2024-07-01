
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"

    java
    application
}
repositories {
    mavenCentral()

    maven(url = "https://jitpack.io")
}

dependencies {
    // Add MiniMessage
    implementation("net.kyori:adventure-text-minimessage:4.12.0")

    // Add Minestom
    implementation("net.minestom:minestom-snapshots:8ea7760e6a")

    // Add chariot, lichess bindings
    implementation("io.github.tors42:chariot:0.0.88")

    // Chesslib
    implementation("com.github.bhlangonijr:chesslib:1.3.3")
}

application {
    mainClass = "com.leodog896.licraft.Main"
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes (
                    "Main-Class" to "com.leodog896.licraft.Main",
                    "Multi-Release" to true
            )
        }

        transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)

        mergeServiceFiles()

        archiveBaseName.set("licraft")
    }

    test { useJUnitPlatform() }

    build { dependsOn(shadowJar) }

    run {
        jar {
            manifest {
                attributes (
                        "Main-Class" to "com.leodog896.licraft.Main",
                        "Multi-Release" to true
                )
            }
        }
    }

    withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/java")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
