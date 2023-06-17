package xyz.cssxsh.mirai.script

import kotlinx.serialization.modules.*
import net.mamoe.mirai.console.data.*
import xyz.cssxsh.mirai.script.cron.*

public object MiraiScriptConfig : ReadOnlyPluginConfig("script") {

    override val serializersModule: SerializersModule = SerializersModule {
        contextual(DataCron)
        contextual(DurationSerializer)
    }

    @ValueName("loop")
    public val loop: MutableMap<String, DataCron> by value()
}