plugins {
    alias(libs.plugins.kotlin.multiplatform)
    // alias(libs.plugins.dokka)
}

val secp256k1Dir = rootDir.resolve("secp256k1-kmp")

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    macosX64()
    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.bignum)
            }
        }
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        val currentTargetName = this.name
        compilations["main"].apply {
            cinterops.create("libsecp256k1") {
                includeDirs.headerFilterOnly(project.file("native/secp256k1/include/"))
                tasks[interopProcessingTaskName].dependsOn(
                    when (currentTargetName) {
                        "iosX64", "iosArm64" -> ":secp256k1-kmp:native:buildSecp256k1Ios"
                        "iosSimulatorArm64" -> ":secp256k1-kmp:native:buildSecp256k1IosSimulatorArm64"
                        "macosX64", "macosArm64" -> ":secp256k1-kmp:native:buildSecp256k1Macos" // Both point to the unified task
                        else -> ":secp256k1-kmp:native:buildSecp256k1Host"
                    }
                )
            }

            val binaryPath =
                when (currentTargetName) {
                    "iosArm64" -> "ios/arm64-iphoneos/libsecp256k1.a"
                    "iosX64" -> "ios/x86_x64-iphonesimulator/libsecp256k1.a"
                    "iosSimulatorArm64" -> "ios/arm64-iphonesimulator/libsecp256k1.a"
                    "macosX64" -> "ios/x86_x64-macosx/libsecp256k1.a"
                    "macosArm64" -> "ios/arm64-x86_x64-macosx/libsecp256k1.a" // Corrected path
                    else -> "host/libsecp256k1.a"
                }

            compilerOptions.configure {
                freeCompilerArgs.addAll(
                    "-include-binary",
                    secp256k1Dir.resolve("native/build/$binaryPath").absolutePath
                )
            }
        }
    }
}
