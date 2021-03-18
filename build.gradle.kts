import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.4.31"

    id("org.jmailen.kotlinter") version "3.3.0"
    id("io.gitlab.arturbosch.detekt") version "1.16.0"
}

group = "com.github.hofiisek"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:1.7.20")

    val kotestVersion = "4.4.3"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.16.0")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    withType<Detekt> {
        this.jvmTarget = "1.8"
    }
}

tasks {
    test {
        useJUnitPlatform()

        finalizedBy(lintKotlin, detekt)
    }
}

detekt {
    baseline = file("config/detekt/detekt.yml")
    parallel = true // Builds the AST in parallel. Rules are always executed in parallel.
}

tasks {
    withType<Wrapper> {
        gradleVersion = "6.6.1"
    }
}
