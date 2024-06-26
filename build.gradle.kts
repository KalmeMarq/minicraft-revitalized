plugins {
    id("java")
}

group = "me.kalmemarq.minicraft"
version = "1.0.0"

val lwjglVersion = "3.3.3"
val jomlVersion = "1.10.5"
val nettyVersion = "4.1.110.Final"
val argOptionParserVersion = "e8a468fef5"
val jacksonVersion = "2.17.1"
val jetbrainAnnotationsVersion = "24.1.0"
val log4jVersion = "2.23.1"

repositories {
    mavenCentral()
    maven {
        setUrl("https://jitpack.io")
    }
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    arrayOf("lwjgl", "lwjgl-glfw", "lwjgl-opengl", "lwjgl-openal", "lwjgl-jemalloc", "lwjgl-stb").forEach {
        implementation("org.lwjgl", it)
        runtimeOnly("org.lwjgl", it, classifier = "natives-windows")
    }
    implementation("org.joml", "joml", jomlVersion)
    arrayOf("netty-buffer", "netty-codec", "netty-common", "netty-handler", "netty-resolver", "netty-transport").forEach {
        implementation("io.netty:$it:$nettyVersion")
    }

    implementation("com.github.KalmeMarq:arg-option-parser:$argOptionParserVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:$jacksonVersion")

    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    compileOnly("org.jetbrains:annotations:$jetbrainAnnotationsVersion")
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
