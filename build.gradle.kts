plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"

    id("net.mamoe.mirai-console") version "2.13.0-RC2"
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
}

group = "xyz.cssxsh.mirai"
version = "1.0.2"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("cssxsh", "mirai-script-plugin")
    licenseFromGitHubProject("AGPL-3.0")
    workingDir = System.getenv("PUBLICATION_TEMP")?.let { file(it).resolve(projectName) }
        ?: buildDir.resolve("publishing-tmp")
    publication {
        artifact(tasks["buildPlugin"])
    }
}

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
    // ruby
    compileOnly("org.jruby:jruby-complete:9.3.9.0")
    testImplementation(kotlin("test"))
    testImplementation("org.slf4j:slf4j-simple:2.0.3")
    testImplementation("net.mamoe:mirai-logging-slf4j:2.13.0-RC2")
}

kotlin {
    explicitApi()
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
    setupConsoleTestRuntime {
        jvmArgs(
            "-Dxyz.cssxsh.mirai.script.js=true",
            "-Dxyz.cssxsh.mirai.script.python=true",
            "-Dxyz.cssxsh.mirai.script.ruby=true"
        )
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

