import java.net.URL

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.51"
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
}

val code: String? by project
task("versionPlus") {
    val file = file("version.properties")
    val properties = org.jetbrains.kotlin.konan.properties.loadProperties(file.path)
    var subVersion = properties.getProperty("subVersion").toString().toInt()
    if (code == null) {
        properties["subVersion"] = (++subVersion).toString()
        properties.store(file.outputStream(), null)
    }
    project.version = project.version.toString() + "-$subVersion"
}

task("buildCode") {
    if (code == null) return@task
    val origin = project.version.toString()
    project.version = "$origin-code"
}
tasks.dokkaJavadoc.configure {
    outputDirectory.set(File("C:\\Users\\Administrator\\Desktop\\Doc\\buffsystem"))
    dokkaSourceSets {
        configureEach {
            externalDocumentationLink {
                url.set(URL("https://doc.skillw.com/pouvoir/"))
            }
        }
    }
}
taboolib {
    if (project.version.toString().contains("-code")) {
        options("skip-kotlin-relocate")
    }

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
    version = "6.0.10-31"
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
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.10")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11800:11800-minimize:mapped")
    compileOnly("ink.ptms.core:v11800:11800-minimize:api")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    compileOnly("com.google.code.gson:gson:2.9.0")
    compileOnly("io.lumine:Mythic-Dist:5.0.3")

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                password = project.findProperty("taboolibPassword").toString()
                username = project.findProperty("taboolibUsername").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}