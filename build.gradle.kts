plugins {
    kotlin("multiplatform") version "1.4.32"
    `maven-publish`
}

group = "me.vital"
version = "1.0-SNAPSHOT"

val jqwikVersion = "1.5.1"

repositories {
    mavenCentral()
}
dependencies {

}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "14"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform {
                includeEngines("junit-jupiter", "jqwik")
            }
        }
    }
    js(LEGACY) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common")) //this prevents code inspection bugs
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-junit5"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib")) //this prevents code inspection bugs
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("stdlib")) //this prevents code inspection bugs
                implementation("org.junit.jupiter:junit-jupiter:5.7.1")
                implementation("net.jqwik:jqwik:${jqwikVersion}")
                implementation("org.assertj:assertj-core:3.19.0")
                implementation(kotlin("test-junit5"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }
}
