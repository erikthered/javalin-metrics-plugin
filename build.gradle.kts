plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    id("org.jetbrains.kotlin.jvm").version("1.3.21")

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.javalin:javalin:2.8.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("commons-io:commons-io:2.6")
    implementation("org.thymeleaf:thymeleaf:3.0.9.RELEASE")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.assertj:assertj-core:3.12.2")
}

application {
    // Define the main class for the application.
    mainClassName = "com.github.erikthered.javalin.example.ExampleAppKt"
}
