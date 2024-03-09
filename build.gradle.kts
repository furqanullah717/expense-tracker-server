plugins {
    kotlin("jvm") version "1.9.22"
    application
    kotlin("plugin.serialization") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"

}

group = "com.codewithfk.expense_tracker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    // Define your main class
    mainClass = "org.example.Application"
}


tasks.withType<JavaExec> {
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.ktor:ktor-server-netty:1.6.5")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.ktor:ktor-server-core:1.6.5")
    implementation("io.ktor:ktor-auth:1.6.5")
    implementation("io.ktor:ktor-auth-jwt:1.6.5")
    implementation("com.auth0:java-jwt:3.18.1")
    implementation("io.ktor:ktor-gson:1.6.5")
    // Database and Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.36.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.36.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.36.2")
    implementation("org.jetbrains.exposed:exposed-java-time:0.36.2") // Make sure this is included

    implementation("com.h2database:h2:1.4.200") // H2 Database
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0") // Use the latest version

    implementation("at.favre.lib:bcrypt:0.9.0")

}

tasks.test {
    useJUnitPlatform()
}
tasks {
    create("stage").dependsOn("installDist")
}
kotlin {
    jvmToolchain(17)
}