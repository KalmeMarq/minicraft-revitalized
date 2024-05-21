import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.kalmemarq.minicraft-revitalized"
version = "1.0.0"

val lwjglVersion: String by project
val lwjglNatives = lwjglNatives()
val jomlVersion: String by project
val fastUtilVersion: String by project
val log4jVersion: String by project
val nettyVersion: String by project
val jacksonVersion: String by project
val jorbisVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    arrayOf("lwjgl", "lwjgl-glfw", "lwjgl-opengl", "lwjgl-jemalloc", "lwjgl-stb", "lwjgl-openal").forEach {
        implementation("org.lwjgl", it)
//      arrayOf("natives-windows", "natives-macos", "natives-linux").forEach { lwjglNatives ->
            runtimeOnly("org.lwjgl", it, classifier = lwjglNatives)
//      }
    }

    arrayOf("netty-buffer", "netty-codec", "netty-common", "netty-handler", "netty-resolver", "netty-transport").forEach {
        implementation("io.netty:${it}:$nettyVersion")
    }

    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.joml", "joml", jomlVersion)
    implementation("it.unimi.dsi:fastutil:$fastUtilVersion")
    implementation("org.jcraft:jorbis:$jorbisVersion")

    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.mojang.ld22.Game"))
        }
    }
}

tasks.register("buildFatJar") {
    dependsOn("shadowJar", "build")
}

fun lwjglNatives(): String {
    return Pair(
        System.getProperty("os.name")!!,
        System.getProperty("os.arch")!!
    ).let { (name, arch) ->
        when {
            arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
                if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                    "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
                else if (arch.startsWith("ppc"))
                    "natives-linux-ppc64le"
                else if (arch.startsWith("riscv"))
                    "natives-linux-riscv64"
                else
                    "natives-linux"
            arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) }     ->
                "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            arrayOf("Windows").any { name.startsWith(it) }                ->
                if (arch.contains("64"))
                    "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
                else
                    "natives-windows-x86"
            else                                                                            ->
                throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
        }
    }
}
