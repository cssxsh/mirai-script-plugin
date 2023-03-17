package xyz.cssxsh.mirai.script.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.script.*
import java.io.*
import java.time.temporal.*
import javax.script.*

@PublishedApi
internal object MiraiScriptCommand : RawCommand(
    owner = MiraiScriptPlugin,
    "script",
    description = "Mirai Script 命令"
) {

    override suspend fun CommandContext.onCommand(args: MessageChain) {
        val path = args.firstOrNull()?.content ?: return
        val file = File(path)
        if (file.exists().not()) return
        val script = file.readText()
        val manager = MiraiScriptPlugin.manager
        val engine = manager.getEngineByExtension(file.extension)
        val bindings = engine.createBindings()
        bindings["context"] = sender
        bindings["args"] = args
        bindings["message"] = originalMessage

        val result = try {
            engine.eval(script, bindings)
        } catch (cause: ScriptException) {
            MiraiScriptPlugin.logger.warning({ "$path 执行错误" }, cause)
            cause.message
        } catch (cause: RuntimeException) {
            MiraiScriptPlugin.logger.warning({ "$path 执行错误" }, cause)
            cause.message
        }

        when (result) {
            is String -> sender.sendMessage(result)
            is Message -> sender.sendMessage(result)
            is Number, is Temporal -> sender.sendMessage(result.toString())
        }
    }
}