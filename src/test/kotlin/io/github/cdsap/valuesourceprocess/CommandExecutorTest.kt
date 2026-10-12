package io.github.cdsap.valuesourceprocess

import org.gradle.process.internal.ExecException
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandExecutorTest {

    @Test
    fun `execute returns stdout captured from ExecOperations`() {
        val execOperations = FakeExecOperations(stdout = "captured-output".toByteArray())
        val executor = CommandExecutor(execOperations)

        val result = executor.execute("echo captured-output")

        assertEquals("captured-output", result)
        assertEquals(listOf("sh", "-c", "echo captured-output"), execOperations.lastCommandLine)
    }

    @Test
    fun `execute runs command through sh -c`() {
        val execOperations = FakeExecOperations(stdout = byteArrayOf())
        val executor = CommandExecutor(execOperations)

        executor.execute("true")

        assertEquals(listOf("sh", "-c", "true"), execOperations.lastCommandLine)
    }

    @Test
    fun `execute returns empty string when ExecOperations fails`() {
        val execOperations = FakeExecOperations(failure = ExecException("command failed"))
        val executor = CommandExecutor(execOperations)

        assertEquals("", executor.execute("exit 1"))
    }

    @Test
    fun `execute returns empty string when command line setup fails`() {
        val execOperations = FakeExecOperations(
            commandLineFailure = ExecException("command setup failed")
        )
        val executor = CommandExecutor(execOperations)

        assertEquals("", executor.execute("echo output"))
    }
}
