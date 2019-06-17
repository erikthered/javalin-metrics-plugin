plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.21")
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.javalin:javalin:2.8.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("commons-io:commons-io:2.6")
    implementation("org.thymeleaf:thymeleaf:3.0.9.RELEASE")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.assertj:assertj-core:3.12.2")
}
