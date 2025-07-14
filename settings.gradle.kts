pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "apollo"

include(":apollo")
include(":iOSLibs")
include(":secp256k1-kmp")
include(":secp256k1-kmp:native")
include(":bip32-ed25519")
