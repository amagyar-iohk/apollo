// import dev.petuska.npm.publish.extension.domain.NpmAccess
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.ByteArrayOutputStream
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    // alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
    // alias(libs.plugins.npm.publish)
    // alias(libs.plugins.swiftpackage)
    // alias(libs.plugins.kover) apply false // https://github.com/Kotlin/kotlinx-kover/issues/747
}

project.description = "Collection of cryptographic methods used across Identus platform."

val currentModuleName = "Apollo"
val appleBinaryName = "ApolloLibrary"
val minimumIosVersion = "15.0"
val minimumMacOSVersion = "13.0"

kotlin {
    applyDefaultHierarchyTemplate()
    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes",)
    }
    jvm()
    androidLibrary {
        namespace = "dev.allain"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()

    // jvm {
    //     withSourcesJar(publish = true)
    //     compilations.all {
    //         compileTaskProvider.configure {
    //             compilerOptions {
    //                 jvmTarget.set(JvmTarget.JVM_17)
    //             }
    //         }
    //     }
    // }
    // androidLibrary {
    //     compileSdk = 34
    //     minSdk = 21
    //     namespace = "org.hyperledger.identus.apollo"
    // }
    // iosArm64 {
    //     swiftCinterop("IOHKSecureRandomGeneration", name)
    //     swiftCinterop("IOHKCryptoKit", name)
    //     binaries.framework {
    //         baseName = appleBinaryName
    //     }
    // }
    // iosX64 {
    //     swiftCinterop("IOHKSecureRandomGeneration", name)
    //     swiftCinterop("IOHKCryptoKit", name)
    //     binaries.framework {
    //         baseName = appleBinaryName
    //     }
    // }
    // iosSimulatorArm64 {
    //     swiftCinterop("IOHKSecureRandomGeneration", name)
    //     swiftCinterop("IOHKCryptoKit", name)
    //     binaries.framework {
    //         baseName = appleBinaryName
    //     }
    // }
    // macosArm64 {
    //     swiftCinterop("IOHKSecureRandomGeneration", name)
    //     swiftCinterop("IOHKCryptoKit", name)
    //     binaries.framework {
    //         baseName = appleBinaryName
    //     }
    // }
    // macosX64 {
    //     swiftCinterop("IOHKSecureRandomGeneration", name)
    //     swiftCinterop("IOHKCryptoKit", name)
    //     binaries.framework {
    //         baseName = appleBinaryName
    //     }
    // }
    // js(IR) {
    //     outputModuleName = currentModuleName
    //     binaries.library()
    //     useCommonJs()
    //     generateTypeScriptDefinitions()
    //     this.compilations["main"].packageJson {
    //         this.version = rootProject.version.toString()
    //     }
    //     this.compilations["test"].packageJson {
    //         this.version = rootProject.version.toString()
    //     }
    //     browser {
    //         webpackTask {
    //             output.library = currentModuleName
    //             output.libraryTarget = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput.Target.VAR
    //         }
    //         testTask {
    //             useKarma { useChromeHeadless() }
    //         }
    //     }
    //     nodejs {
    //         testTask {
    //             useKarma { useChromeHeadless() }
    //         }
    //     }
    // }

    sourceSets {
        commonMain.dependencies {
            // implementation(project(":bip32-ed25519"))
            implementation(libs.serialization.json)
            implementation(libs.bignum)
            implementation(libs.okio)
            implementation(libs.atomicfu)
            implementation(libs.macs.hmac.sha2)
            implementation(libs.hash.hmac.sha2)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            // api(libs.secp256k1.kmp)
            // implementation(libs.secp256k1.kmp.jvm)
            // implementation(libs.secp256k1.kmp.android)
            implementation(libs.guava)
            implementation(libs.bouncycastle)
            implementation(libs.bitcoinjcore)
            implementation(libs.jna.android)
        }
        jvmMain.dependencies {
            // api(libs.secp256k1.kmp)
            // implementation(libs.secp256k1.kmp.jvm)
            implementation(libs.guava)
            implementation(libs.bouncycastle)
            implementation(libs.bitcoinjcore)
            implementation(libs.jna)
        }
        jvmTest.dependencies {
            implementation(libs.junit)
        }
        // jsMain.dependencies {
        //     implementation(npm("elliptic", "6.6.1"))
        //     implementation(npm("@types/elliptic", "6.4.18"))
        //     implementation(npm("@noble/curves", "1.2.0"))
        //     implementation(npm("@stablelib/x25519", "1.0.3"))
        //     implementation(npm("hash.js", "1.1.7"))
        //     implementation(npm("@noble/hashes", "1.3.1"))
        //     implementation(npm("stream-browserify", "3.0.0"))
        //     implementation(npm("buffer", "6.0.3"))
        //     implementation(libs.kotlin.web)
        //     implementation(libs.kotlin.node)
        // }
        // jsTest.dependencies {
        //     implementation(npm("url", "0.11.4"))
        // }
        // nativeMain.dependencies {
        //     implementation(project(":bip32-ed25519"))
        //     implementation(project(":secp256k1-kmp"))
        // }
        // all {
        //     languageSettings {
        //         optIn("kotlin.RequiresOptIn")
        //         optIn("kotlinx.cinterop.ExperimentalForeignApi")
        //     }
        // }
    }

    // multiplatformSwiftPackage {
    //     packageName("Apollo")
    //     swiftToolsVersion("5.9")
    //     targetPlatforms {
    //         iOS { v(minimumIosVersion) }
    //         macOS { v(minimumMacOSVersion) }
    //     }
    //     outputDirectory(File(rootDir, "apollo/build/packages/ApolloSwift"))
    // }
}


// tasks.withType<DokkaTask>().configureEach {
//     moduleName.set(currentModuleName)
//     moduleVersion.set(rootProject.version.toString())
//     description = "This is a Kotlin Multiplatform Library for cryptography"
//     dokkaSourceSets {
//         configureEach {
//             jdkVersion.set(17)
//             languageVersion.set("1.9.22")
//             apiVersion.set("2.0")
//             includes.from(
//                 "docs/Apollo.md",
//                 "docs/Base64.md",
//                 "docs/SecureRandom.md"
//             )
//             sourceLink {
//                 localDirectory.set(projectDir.resolve("src"))
//                 remoteUrl.set(URI("https://github.com/hyperledger-identus/apollo/tree/main/src").toURL())
//                 remoteLineSuffix.set("#L")
//             }
//             externalDocumentationLink {
//                 url.set(URI("https://kotlinlang.org/api/latest/jvm/stdlib/").toURL())
//             }
//             externalDocumentationLink {
//                 url.set(URI("https://kotlinlang.org/api/kotlinx.serialization/").toURL())
//             }
//             externalDocumentationLink {
//                 url.set(URI("https://api.ktor.io/").toURL())
//             }
//             externalDocumentationLink {
//                 url.set(URI("https://kotlinlang.org/api/kotlinx-datetime/").toURL())
//                 packageListUrl.set(URI("https://kotlinlang.org/api/kotlinx-datetime/").toURL())
//             }
//             externalDocumentationLink {
//                 url.set(URI("https://kotlinlang.org/api/kotlinx.coroutines/").toURL())
//             }
//         }
//     }
// }

/**
 * Adds a Swift interop configuration for a library.
 *
 * @param library The name of the library.
 * @param platform The platform for which the interop is being configured.
 */
// fun KotlinNativeTarget.swiftCinterop(library: String, platform: String) {
//     compilations.getByName("main") {
//         cinterops.create(library) {
//             extraOpts = listOf("-compiler-option", "-DNS_FORMAT_ARGUMENT(A)=")
//             when (platform) {
//                 "iosX64", "iosSimulatorArm64" -> {
//                     includeDirs.headerFilterOnly("$rootDir/iOSLibs/$library/build/Release-iphonesimulator/include/")
//                     tasks[interopProcessingTaskName].dependsOn(":iOSLibs:build${library.replaceFirstChar(Char::uppercase)}Iphonesimulator")
//                 }

//                 "iosArm64" -> {
//                     includeDirs.headerFilterOnly("$rootDir/iOSLibs/$library/build/Release-iphoneos/include/")
//                     tasks[interopProcessingTaskName].dependsOn(":iOSLibs:build${library.replaceFirstChar(Char::uppercase)}Iphoneos")
//                 }

//                 "macosX64", "macosArm64" -> {
//                     includeDirs.headerFilterOnly("$rootDir/iOSLibs/$library/build/Release/include/")
//                     tasks[interopProcessingTaskName].dependsOn(":iOSLibs:build${library.replaceFirstChar(Char::uppercase)}Macosx")
//                 }
//             }
//         }
//     }
// }

// === Group: Resource and Test Task Dependencies ===
// val swiftPackageUpdateMinOSVersion =
//     tasks.register("updateMinOSVersion") {
//         group = "multiplatform-swift-package"
//         description =
//             "Updates the minimum OS version of the plists in the xcframework, known issue of the KMP SwiftPackage plugin"
//         dependsOn("createSwiftPackage")

//         val xcframeworkDir = layout.projectDirectory.file("build/packages/ApolloSwift/Apollo.xcframework").asFile

//         doLast {
//             val frameworkPaths =
//                 mapOf(
//                     "ios-arm64/ApolloLibrary.framework" to "ios-arm64/ApolloLibrary.framework/ApolloLibrary",
//                     "ios-arm64_x86_64-simulator/ApolloLibrary.framework" to "ios-arm64_x86_64-simulator/ApolloLibrary.framework/ApolloLibrary"
//                 )

//             frameworkPaths.forEach { (plistFolder, binaryRelativePath) ->
//                 val binaryFile = xcframeworkDir.resolve(binaryRelativePath)
//                 val plistFile = xcframeworkDir.resolve("$plistFolder/Info.plist")

//                 if (binaryFile.exists() && plistFile.exists()) {
//                     val currentMinOS =
//                         ByteArrayOutputStream().use { outputStream ->
//                             providers.exec {
//                                 commandLine("otool", "-l", binaryFile.absolutePath)
//                                 standardOutput = outputStream
//                             }
//                             outputStream
//                                 .toString()
//                                 .lines()
//                                 .firstOrNull { it.contains("minos") }
//                                 ?.trim()
//                                 ?.split(" ")
//                                 ?.lastOrNull()
//                                 ?: throw GradleException("Could not determine min OS version from binary")
//                         }

//                     providers.exec {
//                         commandLine(
//                             "/usr/libexec/PlistBuddy",
//                             "-c",
//                             "Set :MinimumOSVersion $currentMinOS",
//                             plistFile.absolutePath
//                         )
//                     }

//                     println("Updated $plistFile with MinimumOSVersion = $currentMinOS")
//                 } else {
//                     println("Required files not found: binary=$binaryFile, plist=$plistFile")
//                 }
//             }
//         }
//     }

// afterEvaluate {
//     if (tasks.findByName("createSwiftPackage") != null) {
//         tasks.named("createSwiftPackage").configure {
//             finalizedBy(swiftPackageUpdateMinOSVersion)
//         }
//     }
// }

// Configure Dokka tasks uniformly
// tasks.withType<DokkaTask>().configureEach {
//     moduleName.set(currentModuleName)
//     moduleVersion.set(rootProject.version.toString())
// }

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    // if (project.hasProperty("signingInMemoryKey")) {
    signAllPublications()
    // }

    coordinates(group.toString(), "apollo", version.toString())

    pom {
        name.set("Identus Apollo")
        description.set("Collection of cryptographic methods used across Identus platform.")
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


// npmPublish {
//     organization.set("hyperledger")
//     version.set(rootProject.version.toString())
//     access.set(NpmAccess.PUBLIC)
//     packages {
//         access.set(NpmAccess.PUBLIC)
//         named("js") {
//             scope.set("hyperledger")
//             packageName.set("identus-apollo")
//             readme.set(rootDir.resolve("README.md"))
//             packageJson {
//                 author {
//                     name.set("IOG")
//                 }
//                 repository {
//                     type.set("git")
//                     url.set("https://github.com/hyperledger-identus/apollo")
//                 }
//             }
//         }
//     }
//     registries {
//         access.set(NpmAccess.PUBLIC)
//         register("npmjs") {
//             uri.set("https://registry.npmjs.org")
//             authToken.set(System.getenv("NPM_TOKEN"))
//         }
//     }
// }
