package xyz.cssxsh.mirai.script

import kotlinx.coroutines.*
import net.mamoe.mirai.console.events.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.script.cron.*
import java.io.File
import java.time.*
import javax.script.*
import kotlin.coroutines.*

public object MiraiIntervalometer : SimpleListenerHost() {

    private val logger = MiraiLogger.Factory.create(this::class, "mirai-intervalometer")

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        when (exception) {
            is CancellationException -> {
                // ...
            }
            is ExceptionInEventHandlerException -> {
                logger.warning({ "Mira Intervalometer with ${exception.event}" }, exception.cause)
            }
            else -> {
                logger.warning({ "Mira Intervalometer with ${context[CoroutineName]}" }, exception)
            }
        }
    }

    @PublishedApi
    internal fun loop(file: File) {
        launch(CoroutineName("loop<${file.name}>")) {
            logger.info("开始轮询执行脚本 $file")

            val script = file.readText()
            val manager = MiraiScriptPlugin.manager
            val engine = manager.getEngineByExtension(file.extension)
                ?: throw UnsupportedOperationException(file.name)
            val bindings = engine.createBindings()

            bindings["logger"] = logger

            while (isActive) {
                val cron = MiraiScriptConfig.loop[file.path] ?: break
                val execution = cron.toExecutionTime()
                val duration = execution.timeToNextExecution(ZonedDateTime.now()).get()

                delay(timeMillis = duration.toMillis())

                try {
                    engine.eval(script, bindings)
                } catch (cause: RuntimeException) {
                    logger.warning({ "$file 执行错误" }, cause)
                } catch (cause: ScriptException) {
                    logger.warning({ "$file 执行错误" }, cause)
                }
            }
        }
    }

    @EventHandler
    public fun StartupEvent.watch() {
        launch(CoroutineName("startup")) startup@{
            for (file in File("startup").listFiles() ?: return@startup) {
                if (file.canRead().not()) continue

                launch(CoroutineName("startup<${file.name}>")) {
                    logger.info("开始执行脚本 $file")

                    val script = file.readText()
                    val manager = MiraiScriptPlugin.manager
                    val engine = manager.getEngineByExtension(file.extension)
                        ?: throw UnsupportedOperationException(file.name)
                    val bindings = engine.createBindings()

                    bindings["logger"] = logger
                    bindings["event"] = this@watch

                    try {
                        engine.eval(script, bindings)
                    } catch (cause: ScriptException) {
                        logger.warning({ "$file 执行错误" }, cause)
                    } catch (cause: RuntimeException) {
                        logger.warning({ "$file 执行错误" }, cause)
                    }
                }
            }
        }
        MiraiScriptConfig.loop.forEach { (path, _) ->
            val file = File(path)
            if (file.exists()) loop(file = File(path))
        }
    }
}