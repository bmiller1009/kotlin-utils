/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 */
import org.jetbrains.dokka.gradle.DokkaTask
import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources
import java.util.Properties
import java.io.File
import java.time.Duration

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    id("org.jetbrains.kotlin.jvm").version("1.3.61")
    id("org.jetbrains.dokka").version("0.10.0")
    id("net.researchgate.release").version("2.6.0")
    id("java-library")
    id("com.bmuschko.nexus").version("2.3.1")
    id("io.codearte.nexus-staging").version("0.21.2")
    id("de.marcphilipp.nexus-publish").version("0.3.0")
}

group = "org.bradfordmiller"

tasks.create("set-defaults") {
    doFirst {
        val props = Properties()
        val inputStream = File("version.properties").inputStream()
        props.load(inputStream)
        val softwareVersion = props.get("version")!!.toString()

        println("Software Version: " + softwareVersion)

        group = "org.bradfordmiller"
        version = softwareVersion
        inputStream.close()
    }
    doLast {
        println("Current software version is $version")
    }
}

tasks.build {
    dependsOn("set-defaults")
}

//Sample gradle CLI: gradle release -Prelease.useAutomaticVersion=true
release {
    failOnCommitNeeded = true
    failOnPublishNeeded = true
    failOnSnapshotDependencies = true
    failOnUnversionedFiles = true
    failOnUpdateNeeded = true
    revertOnFail = true
    preCommitText = ""
    preTagCommitMessage = "[Gradle Release Plugin] - pre tag commit: "
    tagCommitMessage = "[Gradle Release Plugin] - creating tag: "
    newVersionCommitMessage = "[Gradle Release Plugin] - new version commit: "
    version = "$version"
    versionPropertyFile = "version.properties"
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.1")
}

tasks.matching{it.name != "set-defaults"}.forEach {t ->
    println("Found Task: " + t.name)
    t.dependsOn("set-defaults")
}

tasks {

    "publish" {
        dependsOn("set-defaults")

        doFirst {
            println("Defaults are set. Current software version is $version")
        }
    }

    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }
}

nexusStaging {
    packageGroup = "org.bradfordmiller" //optional if packageGroup == project.getGroup()
}

tasks.register<Jar>("sourcesJars") {
    from(sourceSets.main.get().allJava)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJars") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {

            tasks.matching{it.name != "set-defaults"}.forEach {t ->
                println("Found Task: " + t.name)
                t.dependsOn("set-defaults")
            }

            pom {
                name.set("kotlin-utils")
                description.set("General programming utility library for kotlin")
                url.set("https://github.com/bmiller1009/kotlin-utils")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("bmiller1009")
                        name.set("Bradford Miller")
                        email.set("bfm@bradfordmiller.org")
                    }
                }
                scm {
                    connection.set("scm:git@github.com:bmiller1009/kotlin-utils.git")
                    developerConnection.set("scm:git:ssh://github.com/kotlin-utils.git")
                    url.set("git@github.com:bmiller1009/kotlin-utils.git/")
                }
            }

            from(components["java"])
            artifact(tasks["sourcesJars"])
            artifact(tasks["javadocJars"])
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

val uname: String? by project
val pwd: String? by project

nexusPublishing {
    repositories {
        sonatype {
            username.set(uname)
            password.set(pwd)
        }
    }
    //val duration: java.time.Duration? = Duration.ofSeconds(900)
    clientTimeout.set(Duration.ofSeconds(900))
}