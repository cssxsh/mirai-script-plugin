plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"

    id("net.mamoe.mirai-console") version "2.14.0"
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
}

group = "xyz.cssxsh.mirai"
version = "1.1.0"

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
    api(kotlin("script-util"))
    api(kotlin("scripting-compiler-embeddable"))
    api(kotlin("compiler-embeddable"))
    api("com.cronutils:cron-utils:9.2.1")
    testImplementation(kotlin("test"))
    // lua
    api("org.luaj:luaj-jse:3.0.1")
    // js
    compileOnly("org.graalvm.js:js:22.3.1")
    compileOnly("org.graalvm.js:js-scriptengine:22.3.1")
    // python
    compileOnly("org.python:jython-standalone:2.7.3")
    // ruby
    compileOnly("org.jruby:jruby-complete:9.4.2.0")
    //
    implementation(platform("net.mamoe:mirai-bom:2.14.0"))
    compileOnly("net.mamoe:mirai-console-compiler-common")
    testImplementation("net.mamoe:mirai-logging-slf4j")
    //
    testImplementation(platform("org.slf4j:slf4j-parent:2.0.6"))
    testImplementation("org.slf4j:slf4j-simple")
}

kotlin {
    explicitApi()
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
    setupConsoleTestRuntime {
        jvmArgs(
            "-Dxyz.cssxsh.mirai.script.kotlin.daemon=ture",
            "-Dxyz.cssxsh.mirai.script.command=true",
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

