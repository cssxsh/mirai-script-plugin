package xyz.cssxsh.mirai.script

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import xyz.cssxsh.mirai.script.command.*
import javax.script.*

/**
 * mirai-script-plugin 插件 主类
 */
public object MiraiScriptPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.mirai-script-plugin",
        name = "mirai-script-plugin",
        version = "1.1.0"
    ) {
        author("cssxsh")
    }
) {

    @PublishedApi
    internal val dependencies: MutableMap<String, List<String>> = HashMap()

    public lateinit var manager: ScriptEngineManager
        private set

    @PublishedApi
    internal val isEnableECMAScript: Boolean by lazy { System.getProperty("xyz.cssxsh.mirai.script.js").toBoolean() }

    @PublishedApi
    internal val isEnablePython: Boolean by lazy { System.getProperty("xyz.cssxsh.mirai.script.python").toBoolean() }

    @PublishedApi
    internal val isEnableRuby: Boolean by lazy { System.getProperty("xyz.cssxsh.mirai.script.ruby").toBoolean() }

    override fun PluginComponentStorage.onLoad() {
        if (isEnableECMAScript) dependencies["ECMAScript"] = listOf(
            "org.graalvm.js:js-scriptengine:22.3.1",
            "org.graalvm.js:js:22.3.1"
        )
        if (isEnablePython) dependencies["Python"] = listOf(
            "org.python:jython-standalone:2.7.3"
        )
        if (isEnableRuby) dependencies["Ruby"] = listOf(
            "org.jruby:jruby-complete:9.4.2.0"
        )

        System.setProperty("python.console.encoding", "UTF-8")
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false")
    }

    override fun onEnable() {
        manager = with(jvmPluginClasspath) {
            for ((language, dependency) in dependencies) {
                logger.info("将添加 $language 脚本依赖")
                try {
                    downloadAndAddToPath(pluginSharedLibrariesClassLoader, dependency)
                } catch (cause: Exception) {
                    logger.error("将添加 $language 脚本依赖失败", cause)
                }
            }

            ScriptEngineManager(jvmPluginClasspath.pluginClassLoader)
        }

        MiraiScriptCommand.register()
        MiraiCronCommand.register()
        MiraiIntervalometer.registerTo(globalEventChannel())
    }

    override fun onDisable() {
        MiraiIntervalometer.cancel()
        MiraiCronCommand.unregister()
        MiraiScriptCommand.unregister()
    }
}