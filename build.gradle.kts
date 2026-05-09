plugins {
	kotlin("jvm") version "2.2.10" apply false
	id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
	id("io.github.seggan.kmixin") version "0.1.0" apply false
}

subprojects {
	if (project.name == "module") {
		return@subprojects
	}

	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "com.google.devtools.ksp")
	apply(plugin = "io.github.seggan.kmixin")

	dependencies {
		ksp("io.github.seggan:kmixin:0.1.2")
		implementation("io.github.seggan.kmixin:kmixin-annotations:0.1.2")
	}
}