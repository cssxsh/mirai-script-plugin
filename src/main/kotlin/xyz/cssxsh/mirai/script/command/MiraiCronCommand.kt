package xyz.cssxsh.mirai.script.command

import com.cronutils.model.*
import net.mamoe.mirai.console.command.*
import xyz.cssxsh.mirai.script.*
import xyz.cssxsh.mirai.script.cron.*
import java.io.File

@PublishedApi
internal object MiraiCronCommand : SimpleCommand(
    owner = MiraiScriptPlugin,
    "cron",
    description = "Mirai Script 定时命令",
    overrideContext = CronCommandArgumentContext
) {
    @Handler
    suspend fun ConsoleCommandSender.handle(path: String, cron: Cron) {
        with(MiraiScriptPlugin) {
            MiraiScriptConfig.loop[path] = cron.asData()
            MiraiScriptConfig.save()
        }
        sendMessage("任务已添加")
        val file = File(path)
        MiraiIntervalometer.loop(file)
    }
}