import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kover) apply false // https://github.com/Kotlin/kotlinx-kover/issues/747
}

val appleBinaryName = "ApolloLibrary"
val rustModuleDir = layout.projectDirectory.dir("rust-ed25519-bip32")
val wrapperDir = rustModuleDir.dir("wrapper")
val wasmDir = rustModuleDir.dir("wasm")

val wrapperOutputDir = wrapperDir.dir("build/generated")
val wasmOutputDir = wasmDir.dir("build")
val currentModuleName: String = "Bip32Ed25519"

tasks.register<Copy>("copyGeneratedKotlin") {
    group = "rust"
    description = "Copies Rust-generated Kotlin wrappers."
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    dependsOn("buildRustWrapper")
    dependsOn("prepareAndroidMainArtProfile")
    from(wrapperOutputDir)
    into(layout.buildDirectory.dir("generated"))
}

tasks.register<Copy>("copyNativeLibs") {
    group = "rust"
    description = "Copies native Rust binaries into architecture-specific subdirectories."
    dependsOn("buildRustWrapper")

    inputs.dir(wrapperDir.dir("target"))
    outputs.dir(layout.buildDirectory.dir("generatedResources"))

    // Set the main destination directory for the whole task here
    into(layout.buildDirectory.dir("generatedResources"))

    val archMapping = mapOf(
        "x86_64-unknown-linux-gnu" to "linux-x86-64",
        "aarch64-unknown-linux-gnu" to "linux-aarch64",
        "aarch64-apple-darwin" to "darwin-aarch64",
        "x86_64-apple-darwin" to "darwin-x86-64"
    )
    val targetDirs = mapOf(
        "jvmMain" to listOf("x86_64-apple-darwin", "aarch64-apple-darwin", "aarch64-unknown-linux-gnu", "x86_64-unknown-linux-gnu"),
        "androidMain" to listOf("aarch64-linux-android", "x86_64-linux-android", "i686-linux-android", "armv7-linux-androideabi"),
        "macosArm64Main" to listOf("aarch64-apple-darwin"),
        "macosX64Main" to listOf("x86_64-apple-darwin"),
        "iosX64Main" to listOf("x86_64-apple-ios"),
        "iosSimulatorArm64Main" to listOf("aarch64-apple-ios-sim"),
        "iosArm64Main" to listOf("aarch64-apple-ios")
    )

    // Now, configure each source and its relative destination
    targetDirs.forEach { (target, architectures) ->
        architectures.forEach { archDir ->
            val outputArchDir = archMapping[archDir] ?: archDir
            from(wrapperDir.dir("target/$archDir/release")) {
                include("*.so", "*.dylib", "*.a")
                // Use a simple string for the relative path
                into("$target/libs/$outputArchDir")
            }
        }
    }
}

val copyGeneratedKotlinProvider = tasks.named<Copy>("copyGeneratedKotlin")
val copyNativeLibsProvider = tasks.named<Copy>("copyNativeLibs")

val generatedResourcesDir =
    project.layout.buildDirectory.asFile
        .get()
        .resolve("generatedResources")
val generatedJvmLibsDirs =
    listOf(
        generatedResourcesDir.resolve("jvmMain/libs/darwin-aarch64"),
        generatedResourcesDir.resolve("jvmMain/libs/linux-aarch64"),
        generatedResourcesDir.resolve("jvmMain/libs/darwin-x86-64"),
        generatedResourcesDir.resolve("jvmMain/libs/linux-x86-64")
    )

// tasks.named("build") {
//     dependsOn("kotlinStoreYarnLock")
// }

kotlin {
    jvm {
        withSourcesJar()
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            systemProperty(
                "jna.library.path",
                copyNativeLibsProvider.map { it.outputs.files.asPath }.get()
            )
        }
    }
    androidLibrary {
        namespace = "org.hyperledger.identus.apollo.bip32ed25519"
        compileSdk = 34
        minSdk = 21
    }
    iosArm64 {
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    iosX64 {
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    macosArm64 {
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    macosX64 {
        binaries.framework {
            baseName = appleBinaryName
        }
    }
    // js(IR) {
    //     this.binaries.library()
    //     this.useCommonJs()
    //     generateTypeScriptDefinitions()
    //     browser {
    //         webpackTask {
    //             output.library = currentModuleName
    //             output.libraryTarget = KotlinWebpackOutput.Target.VAR
    //         }
    //         testTask {
    //             useKarma {
    //                 useChromeHeadless()
    //             }
    //         }
    //     }
    //     nodejs {
    //         testTask {
    //             useKarma {
    //                 useChromeHeadless()
    //             }
    //         }
    //     }
    // }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalJsExport")
    }
    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {
            // Keep CommonMain without Uniffi-generated code
            dependencies {
                implementation(libs.atomicfu)
                implementation(libs.serialization.json)
                implementation(libs.okio)
            }
            resources.srcDir(copyGeneratedKotlinProvider.map { it.destinationDir.resolve("commonMain/baselineProfiles") })
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val uniffiMain by creating {
            // New sourceSet for Uniffi-enabled targets
            dependsOn(commonMain)
            kotlin.srcDir(copyGeneratedKotlinProvider.map { it.destinationDir.resolve("commonMain/kotlin") })
        }
        val uniffiTest by creating {
            dependsOn(commonTest)
        }
        val jvmMain by getting {
            dependsOn(uniffiMain)
            kotlin.srcDir(layout.buildDirectory.dir("generated/jvmMain/kotlin"))
            resources.srcDir(copyNativeLibsProvider.map { it.outputs.files.first().resolve("jvmMain/libs") })
            dependencies {
                implementation(libs.jna)
            }
        }
        val jvmTest by getting {
            dependsOn(uniffiTest)
            dependencies {
                implementation(libs.bignum)
                implementation(libs.junit)
            }
        }
        val androidMain by getting {
            dependsOn(uniffiMain)
            kotlin.srcDir(layout.buildDirectory.dir("generated/androidMain/kotlin"))
            resources.srcDir(copyGeneratedKotlinProvider.map { it.destinationDir.resolve("androidMain/baselineProfiles") })
            dependencies {
                implementation("net.java.dev.jna:jna:5.13.0")
            }
        }
        val nativeMain by getting {
            dependsOn(uniffiMain)
            kotlin.srcDir(layout.buildDirectory.dir("generated/nativeMain/kotlin"))
        }
        // val jsMain by getting {
        //     // JS Main is separate and does NOT depend on Uniffi code
        //     dependencies {
        //         implementation(npm("@noble/hashes", "1.3.1"))
        //         implementation(libs.kotlin.web)
        //         implementation(libs.kotlin.node)
        //     }
        // }
        // all {
        //     languageSettings {
        //         optIn("kotlin.RequiresOptIn")
        //         optIn("kotlinx.cinterop.ExperimentalForeignApi")
        //     }
        // }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        val currentTarget = this.name
        // Mapping of Kotlin Native targets to their corresponding Rust architecture directories
        val archMapping = mapOf(
            "macosArm64" to "darwin-aarch64",
            "iosX64" to "x86_64-apple-ios",
            "iosArm64" to "aarch64-apple-ios",
            "iosSimulatorArm64" to "aarch64-apple-ios-sim",
            "macosX64" to "darwin-x86-64"
        )
        val rustArch = archMapping[currentTarget] ?: error("Unsupported target $currentTarget for Rust arch mapping.")

        compilations["main"].cinterops.create("ed25519_bip32_wrapper") {
            // This structure is from your original file.
            val interopDir = layout.buildDirectory.dir("generated/nativeInterop/cinterop/headers/ed25519_bip32_wrapper").get().asFile
            val nativeLibDir = copyNativeLibsProvider.get().outputs.files.first().resolve("${currentTarget}Main/libs/$rustArch")

            packageName("ed25519_bip32_wrapper.cinterop")
            header(interopDir.resolve("ed25519_bip32_wrapper.h"))

            // --- THE FIX ---
            // The error indicates layout.projectDirectory.file() returns a RegularFile directly.
            // We get it and then convert it to a java.io.File with .asFile for the defFile function.
            val defFileObject = layout.projectDirectory.file("src/nativeInterop/cinterop/ed25519_bip32_wrapper.def")
            defFile(defFileObject.asFile)

            // Keep the rest the same as your original
            compilerOpts("-I${interopDir.absolutePath}")
            extraOpts("-libraryPath", nativeLibDir.absolutePath, "-staticLibrary", "libuniffi_ed25519_bip32_wrapper.a")
            tasks[interopProcessingTaskName].dependsOn("prepareRustLibs")
        }
    }

}

// === Group: Rust tasks Tasks ===
tasks.register<Exec>("buildRustWrapper") {
    group = "build"
    description = "Builds Rust binaries for Kotlin multiplatform."
    workingDir = wrapperDir.asFile
    commandLine("bash", "./build-kotlin-library.sh")
    inputs.file(wrapperDir.file("build-kotlin-library.sh"))
    outputs.dirs(wrapperDir.dir("target"), wrapperOutputDir)
}

tasks.register<Exec>("buildRustWasm") {
    group = "rust"
    description = "Builds Rust Wasm binaries for Kotlin multiplatform."
    workingDir = wasmDir.asFile
    commandLine("./build_kotlin_library.sh")
    inputs.file(wasmDir.file("build_kotlin_library.sh"))
    outputs.dir(wasmOutputDir)
}

tasks.register<Copy>("copyWasmOutput") {
    group = "rust"
    description = "Copies Rust-generated Wasm."
    dependsOn("buildRustWasm")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(wasmOutputDir)
    into(
        rootDir
            .resolve("build")
            .resolve("js")
            .resolve("packages")
            .resolve("Apollo")
            .resolve("kotlin")
    )
}

tasks.register<Copy>("copyWasmOutputTest") {
    group = "rust"
    description = "Copies Rust-generated Wasm for testing."
    dependsOn("buildRustWasm")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(wasmOutputDir)
    into(
        rootDir
            .resolve("build")
            .resolve("js")
            .resolve("packages")
            .resolve("Apollo-test")
            .resolve("kotlin")
    )
}

tasks.register("prepareRustLibs") {
    group = "rust"
    description = "Aggregate task for building Rust wrappers and copying outputs."
    dependsOn(
        "buildRustWrapper",
        "buildRustWasm",
        "copyGeneratedKotlin",
        "copyWasmOutput",
        "copyWasmOutputTest",
        "copyNativeLibs"
    )
}

mavenPublishing {
    publishToMavenCentral()

    // if (project.hasProperty("signingInMemoryKey")) {
    signAllPublications()
    // }

    pom {
        name.set("Identus bip32-ed25519")
        description.set("Identus Bip32 HD Keys in Ed25519.")
        url.set("https://hyperledger-identus.github.io/docs/")

        organization {
            name.set("Hyperledger")
            url.set("https://www.hyperledger.org/")
        }

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("hamada147")
                name.set("Ahmed Moussa")
                email.set("ahmed.moussa@iohk.io")
                organization.set("IOG")
                roles.add("developer")
            }
            developer {
                id.set("amagyar-iohk")
                name.set("Allain Magyar")
                email.set("allain.magyar@iohk.io")
                organization.set("IOG")
                roles.add("qc")
            }
            developer {
                id.set("antonbaliasnikov")
                name.set("Anton Baliasnikov")
                email.set("anton.baliasnikov@iohk.io")
                organization.set("IOG")
                roles.add("qc")
            }
            developer {
                id.set("elribonazo")
                name.set("Javier Ribó")
                email.set("javier.ribo@iohk.io")
                organization.set("IOG")
                roles.add("developer")
            }
            developer {
                id.set("goncalo-frade-iohk")
                name.set("Gonçalo Frade")
                email.set("goncalo.frade@iohk.io")
                organization.set("IOG")
                roles.add("developer")
            }
            developer {
                id.set("curtis-h")
                name.set("Curtis Harding")
                email.set("curtis.harding@iohk.io")
                organization.set("IOG")
                roles.add("developer")
            }
            developer {
                id.set("cristianIOHK")
                name.set("Cristian Gonzalez")
                email.set("cristian.castro@iohk.io")
                organization.set("IOG")
                roles.add("developer")
            }
            developer {
                id.set("yshyn-iohk")
                name.set("Yurii Shynbuiev")
                email.set("yurii.shynbuiev@iohk.io")
                organization.set("IOG")
                roles.add("developer")
            }
        }

        scm {
            connection.set("scm:git:git://git@github.com/hyperledger/identus-apollo.git")
            developerConnection.set("scm:git:ssh://git@github.com/hyperledger/identus-apollo.git")
            url.set("https://github.com/hyperledger/identus-apollo")
        }
    }
}
