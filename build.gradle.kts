plugins {
    kotlin("jvm") version "1.8.0"
    application
    `maven-publish`
    `signing`
    `kotlin-dsl`
}

group = "io.github.cdsap"
version = "0.1.0"


repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.gradle.enterprise:com.gradle.enterprise.gradle.plugin:3.12.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

configure<JavaPluginExtension> {
    withJavadocJar()
    withSourcesJar()
}


publishing {
    repositories {
        maven {
            name = "Snapshots"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

            credentials {
                username = System.getenv("USERNAME_SNAPSHOT")
                password = System.getenv("PASSWORD_SNAPSHOT")
            }
        }
        maven {
            name = "Release"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = System.getenv("USERNAME_SNAPSHOT")
                password = System.getenv("PASSWORD_SNAPSHOT")
            }
        }
    }
    publications {
        create<MavenPublication>("libPublication") {
            from(components["java"])
            artifactId = "commandline-value-source"
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                scm {
                    connection.set("scm:git:git://github.com/cdsap/CommandLineValueSource/")
                    url.set("https://github.com/cdsap/CommandLineValueSource/")
                }
                name.set("commandline-value-source")
                url.set("https://github.com/cdsap/CommandLineValueSource/")
                description.set(
                    "ValueSource returning the output of the command"
                )
                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("cdsap")
                        name.set("Inaki Villar")
                    }
                }
            }
        }
    }
}

if (extra.has("signing.keyId")) {
    afterEvaluate {
        configure<SigningExtension> {
            (extensions.getByName("publishing") as
                PublishingExtension).publications.forEach {
                sign(it)
            }
        }
    }
}
