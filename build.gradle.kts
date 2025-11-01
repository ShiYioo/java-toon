plugins {
    kotlin("jvm") version "2.2.20"
    `java-library`
    `maven-publish`
}

group = "org.shiyi"
version = "1.0-SNAPSHOT"
description = "A Kotlin and Java library for toon implementation"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin standard library
    implementation(kotlin("stdlib"))

    // Testing dependencies
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
    explicitApi()
}

// Publishing configuration
publishing {
    publications.register<MavenPublication>("maven") {
        from(components["java"])

        pom {
            name = "java-toon"
            description = "A Kotlin library for toon development"
            url = "https://github.com/ShiYioo/java-toon"

            developers {
                developer {
                    id = "shiyi"
                    name = "ShiYi"
                    email = "141152739+ShiYioo@users.noreply.github.com"
                }
            }

            licenses {
                license {
                    name = "MIT License"
                    url = "https://opensource.org/licenses/MIT"
                }
            }

            scm {
                connection = "scm:git:https://github.com/ShiYioo/java-toon.git"
                developerConnection = "scm:git:https://github.com/ShiYioo/java-toon.git"
                url = "https://github.com/ShiYioo/java-toon"
            }
        }
    }

    repositories {
        // Local Maven repository (for local development and testing)
        mavenLocal()

        // Uncomment to publish to Maven Central (requires OSSRH account setup)
        // maven {
        //     url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        //     credentials {
        //         username = System.getenv("OSSRH_USERNAME")
        //         password = System.getenv("OSSRH_PASSWORD")
        //     }
        // }
    }
}

// Source JAR task
tasks.register<Jar>("sourcesJar") {
    archiveClassifier = "sources"
    from(sourceSets["main"].allSource)
}
