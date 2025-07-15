import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.ktlint)
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
//    apply(plugin = "org.jetbrains.kotlinx.kover")
    ktlint {
        verbose.set(true)
        outputToConsole.set(true)
        filter {
            exclude { it.file.path.contains("external") }
            exclude { it.file.path.contains("generated") }
        }
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
