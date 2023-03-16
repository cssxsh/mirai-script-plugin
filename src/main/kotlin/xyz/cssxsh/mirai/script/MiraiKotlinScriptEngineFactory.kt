package xyz.cssxsh.mirai.script

import org.jetbrains.kotlin.cli.common.repl.*
import org.jetbrains.kotlin.script.jsr223.*
import javax.script.*
import net.mamoe.mirai.console.internal.plugin.*
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
    private val shared: ClassLoader by lazy {
        val classLoader = this::class.java.classLoader as JvmPluginClassLoaderN
        val classpath = classLoader.openaccess
        classpath.pluginSharedLibrariesClassLoader
    }

    override fun getScriptEngine(): ScriptEngine {
        return if (daemon) {
            KotlinJsr223JvmDaemonCompileScriptEngine(
                this,
                KotlinJars.compilerWithScriptingClasspath,
                scriptCompilationClasspathFromContext("kotlin-script-util.jar", classLoader = shared, wholeClasspath = true),
                KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
                { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
                arrayOf(Bindings::class)
            )
        } else {
            KotlinJsr223JvmLocalScriptEngine(
                this,
                scriptCompilationClasspathFromContext("kotlin-script-util.jar", classLoader = shared, wholeClasspath = true),
                KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
                { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
                arrayOf(Bindings::class)
            )
        }
    }
}