# [Mirai Script Plugin](https://github.com/cssxsh/mirai-script-plugin)

> Mirai Script 前置插件

[Mirai Console](https://github.com/mamoe/mirai-console) 的前置插件，用于 `javax.script.ScriptEngine` 框架的初始化

[![maven-central](https://img.shields.io/maven-central/v/xyz.cssxsh.mirai/mirai-script-plugin)](https://search.maven.org/artifact/xyz.cssxsh.mirai/mirai-script-plugin)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/5b3eefaa267f49518265a9a321443e46)](https://www.codacy.com/gh/cssxsh/mirai-script-plugin/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cssxsh/mirai-script-plugin&amp;utm_campaign=Badge_Grade)

本插件将加载以下 `ScriptEngineFactory`
*   [Lua](https://search.maven.org/artifact/org.luaj/luaj-jse)
*   [KotlinScript](https://search.maven.org/artifact/org.jetbrains.kotlin/kotlin-script-util)
*   [ECMAScript](https://search.maven.org/artifact/org.graalvm.js/js-scriptengine) `xyz.cssxsh.mirai.script.js`
*   [Python](https://search.maven.org/artifact/org.python/jython-standalone) `xyz.cssxsh.mirai.script.python`
*   [Ruby](https://search.maven.org/artifact/org.jruby/jruby-complete) `xyz.cssxsh.mirai.script.ruby`

其中 `Lua` 是默认加载的  
`ECMAScript`, `Python`, `Ruby` 需要设置 `properties`

## 在 Mirai Console Plugin 项目中引用

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("xyz.cssxsh.mirai:mirai-script-plugin:${version}")
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
}
```

## 安装

### MCL 指令安装

**请确认 mcl.jar 的版本是 2.1.0+**  
`./mcl --update-package xyz.cssxsh.mirai:mirai-script-plugin --channel maven-stable --type plugin`

### 手动安装

1.  从 [Releases](https://github.com/cssxsh/mirai-script-plugin/releases) 或者 [Maven](https://repo1.maven.org/maven2/xyz/cssxsh/mirai/mirai-script-plugin/) 下载 `mirai2.jar`
2.  将其放入 `plugins` 文件夹中