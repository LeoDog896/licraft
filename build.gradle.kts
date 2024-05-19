import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"

    java
    application
}
repositories {
    // maven central
    mavenCentral()

    maven(url = "https://jitpack.io")
}

dependencies {
    // Add MiniMessage
    implementation("net.kyori:adventure-text-minimessage:4.12.0")

    // Compile Minestom into project
    implementation("net.minestom:minestom-snapshots:33dff6f458")
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
