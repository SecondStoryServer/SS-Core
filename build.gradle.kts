import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.70"
    maven
}

group = "me.syari.ss.core"
version = "LATEST"

repositories {
    mavenCentral()
    maven ("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    implementation("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val jar by tasks.getting(Jar::class) {
    from(configurations.compile.get().map { if (it.isDirectory) it else zipTree(it) })
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}

tasks.getByName<Upload>("uploadArchives") {
    repositories {
        withConvention(MavenRepositoryHandlerConvention::class) {
            mavenDeployer {
                withGroovyBuilder {
                    "repository"("url" to uri("$buildDir/m2/releases"))
                    "snapshotRepository"("url" to uri("$buildDir/m2/snapshots"))
                }
                pom.project {
                    withGroovyBuilder {
                        "parent" {
                            "groupId"(group)
                            "artifactId"(rootProject.name)
                            "version"(version as String)
                        }
                    }
                }
            }
        }
    }
}