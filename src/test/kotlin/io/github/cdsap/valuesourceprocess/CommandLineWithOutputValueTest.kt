package io.github.cdsap.valuesourceprocess

import org.gradle.api.provider.Property
import org.gradle.process.ExecOperations
import org.gradle.process.internal.ExecException
import org.gradle.testfixtures.ProjectBuilder
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
}
