plugins {
    kotlin("jvm") version "2.2.20"
    `java-library`
    `maven-publish`
    signing
}

group = "io.github.shiyioo"
version = "0.0.1"
description = "A Kotlin and Java library for toon implementation"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin standard library
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // Jackson for JSON/Object conversion (Kotlin support)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.1")

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
        groupId = "io.github.shiyioo"
        artifactId = "java-toon"
        version = project.version.toString()

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

    // 配置本地发布目录，用于生成bundle
    repositories {
        maven {
            name = "LocalRepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

// 配置签名任务
signing {
    // 优先从环境变量读取，其次从 gradle.properties 读取
    val signingKeyId = System.getenv("SIGNING_KEY_ID")
        ?: findProperty("signing.keyId") as String?
    val signingPassword = System.getenv("SIGNING_PASSWORD")
        ?: findProperty("signing.password") as String?

    // 只有在具备签名凭据时才进行签名
    if (!signingKeyId.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        // 使用 useGpgCmd() 让 Gradle 使用系统的 gpg 命令
        useGpgCmd()
        sign(publishing.publications["maven"])
    } else {
        // 未配置签名凭据时，跳过签名（用于本地开发）
        isRequired = false
    }
}

// 创建bundle文件的任务
tasks.register<Zip>("createBundle") {
    group = "publishing"
    description = "创建用于手动上传到Maven Central的bundle文件"

    dependsOn("publishMavenPublicationToLocalRepoRepository")

    archiveFileName.set("${project.name}-${project.version}-bundle.zip")
    destinationDirectory.set(layout.buildDirectory.dir("bundle"))

    from(layout.buildDirectory.dir("repo")) {
        include("**/*")
    }

    doLast {
        println("Bundle文件已创建: ${archiveFile.get().asFile.absolutePath}")
        println("请访问 https://central.sonatype.com/publishing 上传此文件")
    }
}
