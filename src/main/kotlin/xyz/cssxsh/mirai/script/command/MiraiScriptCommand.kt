package xyz.cssxsh.mirai.script.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.script.*
import java.time.temporal.*
import javax.script.*

@PublishedApi
internal object MiraiScriptCommand : RawCommand(
    owner = MiraiScriptPlugin,
    "script",
    description = "Lua Script 命令"
) {

    override suspend fun CommandContext.onCommand(args: MessageChain) {
        val type = args.firstOrNull()?.content ?: return
        val script = originalMessage.contentToString().substringAfter(type, "")
        if (script.isBlank()) {
            sender.sendMessage("脚本从第二行开始")
            return
        }
        val manager = MiraiScriptPlugin.manager
        val engine = manager.getEngineByName(type) ?: manager.getEngineByExtension(type)
        if (engine == null) {
            sender.sendMessage("未启用 $type 脚本")
            return
        }
        val bindings = engine.createBindings()
        bindings["sender"] = sender
        bindings["message"] = originalMessage

        val result = try {
            engine.eval(script, bindings)
        } catch (cause: ScriptException) {
            MiraiScriptPlugin.logger.warning({ "$type 执行错误" }, cause)
            cause.message
        } catch (cause: RuntimeException) {
            MiraiScriptPlugin.logger.warning({ "$type 执行错误" }, cause)
            cause.message
        }

        when (result) {
            is String -> sender.sendMessage(result)
            is Message -> sender.sendMessage(result)
            is Number, is Temporal -> sender.sendMessage(result.toString())
        }
    }
}