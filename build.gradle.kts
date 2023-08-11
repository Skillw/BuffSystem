import java.net.URL

plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.dokka") version "1.7.20"
    id("io.codearte.nexus-staging") version "0.30.0"
}

tasks.dokkaJavadoc.configure {
    suppressInheritedMembers.set(true)
    suppressObviousFunctions.set(false)
    dokkaSourceSets {
        configureEach {
            externalDocumentationLink {
                url.set(URL("https://doc.skillw.com/pouvoir/"))
            }
            externalDocumentationLink {
                url.set(URL("https://doc.skillw.com/attsystem/"))
            }
            externalDocumentationLink {
                url.set(URL("https://docs.oracle.com/javase/8/docs/api/"))
            }
            externalDocumentationLink {
                url.set(URL("https://doc.skillw.com/bukkit/"))
            }
        }
    }
}

val order: String? by project

task("info") {
    println(project.name + "-" + project.version)
    println(project.version.toString())
}
taboolib {
    project.version = project.version.toString() + (order?.let { "-$it" } ?: "")

    description {
        contributors {
            name("Glom_")
        }
        dependencies {
            name("AttributeSystem").optional(true)
            name("SX-Attribute").optional(true)
            name("AttributePlus").optional(true)
            name("OriginAttribute").optional(true)
            name("Pouvoir")
        }
    }
    install("module-configuration")
    install("module-lang")
    install("platform-bukkit")
    install("module-chat")
    install("module-nms")
    install("module-nms-util")
    install("common")
    install("module-metrics")
    install("common-5")

    classifier = null
    version = "6.0.11-31"
}

repositories {
    mavenCentral()
    maven { url = uri("https://mvn.lumine.io/repository/maven-public/") }
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}
dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.20")
    compileOnly("ink.ptms.core:v11800:11800-minimize:mapped")
    compileOnly("ink.ptms.core:v11800:11800-minimize:api")
    compileOnly("com.google.code.gson:gson:2.9.0")
    compileOnly("io.lumine:Mythic-Dist:5.0.3")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.register<Jar>("buildAPIJar") {
    dependsOn(tasks.compileJava, tasks.compileKotlin)
    from(tasks.compileJava, tasks.compileKotlin)
    includeEmptyDirs = false
    include { it.isDirectory or it.name.endsWith(".class") or it.name.endsWith(".kotlin_module") }
    archiveClassifier.set("api")
}

tasks.register<Jar>("buildJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

tasks.register<Jar>("buildSourcesJar") {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}


publishing {
    repositories {
        maven {
            url = if (project.version.toString().contains("-SNAPSHOT")) {
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            } else {
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
            credentials {
                username = project.findProperty("username").toString()
                password = project.findProperty("password").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")

            }
        }
        mavenLocal()
    }
    publications {
        create<MavenPublication>("library") {
            artifact(tasks["buildAPIJar"]) { classifier = classifier?.replace("-api", "") }
            artifact(tasks["buildJavadocJar"])
            artifact(tasks["buildSourcesJar"])
            version = project.version.toString()
            groupId = project.group.toString()
            pom {
                name.set(project.name)
                description.set("Bukkit Buff Engine Plugin.")
                url.set("https://github.com/Glom-c/BuffSystem/")

                dependencies {
                    compileOnly("com.skillw.pouvoir:Pouvoir:1.6.4-8")
                    compileOnly("com.skillw.attributesystem:AttributeSystem:2.1.0-beta-2")
                }

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/Glom-c/BuffSystem/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("Skillw")
                        name.set("Glom_")
                        email.set("glom@skillw.com")
                    }
                }
                scm {
                    connection.set("scm:git:git:https://github.com/Glom-c/BuffSystem.git")
                    developerConnection.set("scm:git:ssh:https://github.com/Glom-c/BuffSystem.git")
                    url.set("https://github.com/Glom-c/BuffSystem.git")
                }
            }
        }
    }
}

nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    username = project.findProperty("username").toString()
    password = project.findProperty("password").toString()
    packageGroup = "com.skillw"
}

signing {
    sign(publishing.publications.getAt("library"))
}