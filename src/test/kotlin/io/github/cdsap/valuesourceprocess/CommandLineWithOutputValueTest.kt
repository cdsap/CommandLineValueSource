package io.github.cdsap.valuesourceprocess

import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.gradle.process.JavaExecSpec
import org.gradle.process.internal.ExecException
import org.gradle.testfixtures.ProjectBuilder
import java.io.OutputStream
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandLineWithOutputValueTest {

    @Test
    fun `successful command returns decoded stdout`() {
        val valueSource = testableValueSource(
            FakeExecOperations(stdout = "hello".toByteArray()),
            command = "printf 'hello'"
        )

        assertEquals("hello", valueSource.obtain())
    }

    @Test
    fun `failed command returns empty string`() {
        val valueSource = testableValueSource(
            FakeExecOperations(failure = ExecException("boom")),
            command = "exit 1"
        )

        assertEquals("", valueSource.obtain())
    }

    private fun testableValueSource(
        execOperations: ExecOperations,
        command: String
    ): CommandLineWithOutputValue {
        val project = ProjectBuilder.builder().build()
        val commands = project.objects.property(String::class.java)
        commands.set(command)
        return TestableCommandLineWithOutputValue(
            execOperationsOverride = execOperations,
            parametersOverride = TestParameters(commands)
        )
    }

    private class TestParameters(
        override val commands: Property<String>
    ) : CommandLineWithOutputValue.Parameters

    private class TestableCommandLineWithOutputValue(
        private val execOperationsOverride: ExecOperations,
        private val parametersOverride: Parameters
    ) : CommandLineWithOutputValue() {
        override val execOperations: ExecOperations
            get() = execOperationsOverride

        override fun getParameters(): Parameters = parametersOverride
    }

    private class FakeExecOperations(
        private val stdout: ByteArray = byteArrayOf(),
        private val failure: ExecException? = null
    ) : ExecOperations {
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
                    "setCommandLine", "commandLine", "executable", "setExecutable",
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
            error("javaexec is not used by CommandLineWithOutputValue")
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
    }

    private class StreamHolder {
        var standardOutput: OutputStream? = null
        var errorOutput: OutputStream? = null
    }
}
