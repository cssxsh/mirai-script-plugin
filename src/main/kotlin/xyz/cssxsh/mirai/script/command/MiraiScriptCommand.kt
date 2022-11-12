package xyz.cssxsh.mirai.script.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.script.*
import java.time.temporal.Temporal

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
        bindings["originalMessage"] = originalMessage

        when (val result = engine.eval(script, bindings)) {
            is String -> sender.sendMessage(result)
            is Message -> sender.sendMessage(result)
            is Number, is Temporal -> sender.sendMessage(result.toString())
        }
    }
}