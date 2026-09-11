package io.github.cdsap.valuesourceprocess

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommandLineWithOutputValueFunctionalTest {

    @Test
    fun `execute returns command stdout`() {
        val project = ProjectBuilder.builder().build()
        val output = project.execute("echo hello-value-source").get()

        assertContains(output, "hello-value-source")
    }

    @Test
    fun `command line value source returns empty string when command fails`() {
        val project = ProjectBuilder.builder().build()
        val output = project.providers.of(CommandLineWithOutputValue::class.java) {
            parameters.commands.set("exit 1")
        }.get()

        assertEquals("", output)
    }

    @Test
    fun `jStat provider is wired to command line value source`() {
        val project = ProjectBuilder.builder().build()
        val provider = project.jStat("NonExistentProcessNameForTest")

        assertTrue(provider.isPresent)
        // No matching process is fine; the provider must still resolve without throwing.
        provider.get()
    }
}
