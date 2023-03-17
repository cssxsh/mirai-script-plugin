package xyz.cssxsh.mirai.script

import org.jetbrains.kotlin.cli.common.environment.*
import org.jetbrains.kotlin.cli.common.repl.*
import org.jetbrains.kotlin.script.jsr223.*
import javax.script.*
import net.mamoe.mirai.console.internal.plugin.*
import java.io.*
import kotlin.script.experimental.jvm.util.*

/**
 * @see KotlinJsr223JvmLocalScriptEngineFactory
 * @see KotlinJsr223JvmDaemonLocalEvalScriptEngineFactory
 */
public class MiraiKotlinScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {
    private val daemon: Boolean by lazy {
        java.lang.Boolean.getBoolean("xyz.cssxsh.mirai.script.kotlin.daemon")
    }

    @Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
    private val templateClasspath: List<File> by lazy {
        val classLoader = (this::class.java.classLoader as? JvmPluginClassLoaderN)
            ?.openaccess?.pluginSharedLibrariesClassLoader
            ?: this::class.java.classLoader
        scriptCompilationClasspathFromContext(
            "kotlin-script-util.jar",
            classLoader = classLoader,
            wholeClasspath = true
        )
    }

    init {
        setIdeaIoUseFallback()
    }

    override fun getScriptEngine(): ScriptEngine {
        return if (daemon) {
            KotlinJsr223JvmDaemonCompileScriptEngine(
                factory = this,
                compilerClasspath = KotlinJars.compilerWithScriptingClasspath,
                templateClasspath = templateClasspath,
                templateClassName = KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
                getScriptArgs = { ctx, types ->
                    ScriptArgsWithTypes(
                        arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)),
                        types ?: emptyArray()
                    )
                },
                scriptArgsTypes = arrayOf(Bindings::class)
            )
        } else {
            KotlinJsr223JvmLocalScriptEngine(
                factory = this,
                templateClasspath = templateClasspath,
                templateClassName = KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
                getScriptArgs = { ctx, types ->
                    ScriptArgsWithTypes(
                        arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)),
                        types ?: emptyArray()
                    )
                },
                scriptArgsTypes = arrayOf(Bindings::class)
            )
        }
    }
}