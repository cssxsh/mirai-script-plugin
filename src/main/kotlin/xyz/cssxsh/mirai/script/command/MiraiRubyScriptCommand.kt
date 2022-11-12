package xyz.cssxsh.mirai.script.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.message.data.*
import xyz.cssxsh.mirai.script.*

@PublishedApi
internal object MiraiRubyScriptCommand : RawCommand(
    owner = MiraiScriptPlugin,
    "ruby",
    description = "Ruby Script 命令"
) {

    override suspend fun CommandContext.onCommand(args: MessageChain) {
        val script = originalMessage.contentToString().substringAfter("ruby", "")
        if (script.isBlank()) {
            sender.sendMessage("脚本从第二行开始")
            return
        }
        val engine = MiraiScriptPlugin.manager.getEngineByName("ruby")
        if (engine == null) {
            sender.sendMessage("未启用 Ruby 脚本")
            return
        }
        val bindings = engine.createBindings()
        bindings["sender"] = sender
        bindings["originalMessage"] = originalMessage
        val result = engine.eval(script, bindings)

        sender.sendMessage(result.toString())
    }
}