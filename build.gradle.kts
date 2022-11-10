plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"

    id("net.mamoe.mirai-console") version "2.13.0-RC2"
}

group = "xyz.cssxsh.mirai"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.mamoe:mirai-console-compiler-common:2.13.0")
    // lua
    api("org.luaj:luaj-jse:3.0.1")
    // js
    compileOnly("org.graalvm.js:js:22.2.0")
    compileOnly("org.graalvm.js:js-scriptengine:22.2.0")
    // python
    compileOnly("org.python:jython-standalone:2.7.3")
    testImplementation(kotlin("test"))
    testImplementation("org.slf4j:slf4j-simple:2.0.3")
    testImplementation("net.mamoe:mirai-logging-slf4j:2.13.0-RC2")
}

kotlin {
    explicitApi()
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
}

tasks {
    test {
        useJUnitPlatform()
    }
}

