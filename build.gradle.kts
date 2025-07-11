import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

group = "dev.allain"

allprojects {
    group = rootProject.group

    repositories {
        google()
        mavenCentral()
    }

    rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
        rootProject.the<YarnRootExtension>().reportNewYarnLock = true
        rootProject.the<YarnRootExtension>().yarnLockAutoReplace = true
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlinx.kover")
    ktlint {
        verbose.set(true)
        outputToConsole.set(true)
        filter {
            exclude { it.file.path.contains("external") }
            exclude { it.file.path.contains("generated") }
        }
    }
    koverReport {
        filters {
            excludes {
                classes("org.hyperledger.identus.apollo.utils.bip39.wordlists.*")
            }
        }
    }
}

tasks.register("debugSigningProperties") {
    doLast {
        println("--- Debugging Signing Properties ---")
        val key = findProperty("signingInMemoryKey") as? String
        val password = findProperty("signingInMemoryKeyPassword") as? String

        if (key.isNullOrBlank()) {
            println("❌ signingInMemoryKey: IS MISSING OR EMPTY")
        } else {
            println("✅ signingInMemoryKey: Is present (Length: ${key.length})")
        }
        if (password.isNullOrBlank()) {
            println("❌ signingInMemoryKeyPassword: IS MISSING OR EMPTY")
        } else {
            println("✅ signingInMemoryKeyPassword: Is present (Length: ${password.length})")
        }
        println("------------------------------------")
    }
}

/**
 * The `javadocJar` variable is used to register a `Jar` task to generate a Javadoc JAR file.
 * The Javadoc JAR file is created with the classifier "javadoc" and it includes the HTML documentation generated
 * by the `dokkaHtml` task.
 */
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    // from(tasks.dokkaHtml)
}

tasks.wrapper {
    gradleVersion = "8.13"
    distributionType = Wrapper.DistributionType.ALL
}
