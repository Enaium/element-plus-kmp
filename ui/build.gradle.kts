import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    iosArm64()
    iosSimulatorArm64()

    android {
        namespace = "cn.enaium.elementplus.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.ui)
            api(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.uiTest)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


group = "cn.enaium.elementplus"
version = "1.1.1"

mavenPublishing {
    coordinates(project.group.toString(), rootProject.name, project.version.toString())

    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("Element Plus KMP")
        description.set("A Kotlin Multiplatform / Compose Multiplatform port of Element Plus.")
        inceptionYear.set("2026")
        url.set("https://github.com/Enaium/element-plus-kmp")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("enaium")
                name.set("Enaium")
                url.set("https://github.com/Enaium")
            }
        }

        scm {
            url.set("https://github.com/Enaium/element-plus-kmp")
            connection.set("scm:git:git://github.com/Enaium/element-plus-kmp.git")
            developerConnection.set("scm:git:ssh://git@github.com/Enaium/element-plus-kmp.git")
        }
    }
}
