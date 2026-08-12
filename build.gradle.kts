repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "2.4.0"
    application
}
group = "dev.apollointhehouse.walker"
version = "1.0-SNAPSHOT"

val lwjglNatives = Pair(
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
        arrayOf("Windows").any { name.startsWith(it) }                ->
            "natives-windows"
        else                                                                            ->
            throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}


repositories {
    mavenCentral()
}

dependencies {
    // logging
    implementation(libs.bundles.log4j)
    runtimeOnly(libs.jackson.databind)

    // LWJGL
    implementation(platform("org.lwjgl:lwjgl-bom:${libs.versions.lwjglVersion.get()}"))
    implementation(libs.bundles.lwjgl)
    implementation(libs.joml)
    implementation("org.lwjgl:lwjgl::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-assimp::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-openal::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-stb::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-sdl::$lwjglNatives")

    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(25)

    sourceSets {
        main {
            kotlin.setSrcDirs(listOf("src/main/kotlin"))
        }
    }
}

application {
    mainClass.set("dev.apollointhehouse.walker.MainKt")
    applicationDefaultJvmArgs = listOf("--add-opens=java.base/sun.misc=ALL-UNNAMED", "--enable-native-access=ALL-UNNAMED")
}

tasks.test {
    useJUnitPlatform()
}