package io.github.cdsap.valuesourceprocess

import org.gradle.api.Action
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.gradle.process.JavaExecSpec
import org.gradle.process.internal.ExecException
import java.io.OutputStream
import java.lang.reflect.Proxy

internal class FakeExecOperations(
    private val stdout: ByteArray = byteArrayOf(),
    private val failure: ExecException? = null
) : ExecOperations {
    var lastCommandLine: List<String> = emptyList()
        private set

    override fun exec(action: Action<in ExecSpec>): ExecResult {
        if (failure != null) {
            throw failure
        }
        val holder = StreamHolder()
        @Suppress("UNCHECKED_CAST")
        val spec = Proxy.newProxyInstance(
            ExecSpec::class.java.classLoader,
            arrayOf(ExecSpec::class.java)
        ) { _, method, args ->
            when (method.name) {
                "setCommandLine", "commandLine" -> {
                    lastCommandLine = flattenCommandLineArgs(args)
                    null
                }
                "executable", "setExecutable",
                "workingDir", "setWorkingDir", "environment", "setEnvironment",
                "args", "setArgs" -> null
                "setStandardOutput" -> {
                    holder.standardOutput = args?.get(0) as OutputStream
                    null
                }
                "setErrorOutput" -> {
                    holder.errorOutput = args?.get(0) as OutputStream
                    null
                }
                "getStandardOutput" -> holder.standardOutput
                "getErrorOutput" -> holder.errorOutput
                "isIgnoreExitValue", "getIgnoreExitValue" -> false
                "getCommandLine", "getArgs" -> emptyList<String>()
                else -> defaultValue(method.returnType)
            }
        } as ExecSpec

        action.execute(spec)
        holder.standardOutput?.write(stdout)
        return object : ExecResult {
            override fun getExitValue(): Int = 0
            override fun assertNormalExitValue(): ExecResult = this
            override fun rethrowFailure(): ExecResult = this
        }
    }

    override fun javaexec(action: Action<in JavaExecSpec>): ExecResult {
        error("javaexec is not used by CommandExecutor")
    }

    private fun flattenCommandLineArgs(args: Array<out Any>?): List<String> {
        if (args == null || args.isEmpty()) {
            return emptyList()
        }
        val first = args[0]
        return when (first) {
            is Array<*> -> first.map { it.toString() }
            is Collection<*> -> first.map { it.toString() }
            else -> args.map { it.toString() }
        }
    }

    private fun defaultValue(returnType: Class<*>): Any? = when (returnType) {
        java.lang.Void.TYPE -> null
        java.lang.Boolean.TYPE -> false
        java.lang.Integer.TYPE -> 0
        java.lang.Long.TYPE -> 0L
        List::class.java -> emptyList<Any>()
        Map::class.java -> emptyMap<Any, Any>()
        else -> null
    }

    private class StreamHolder {
        var standardOutput: OutputStream? = null
        var errorOutput: OutputStream? = null
    }
}
