package io.github.cdsap.valuesourceprocess

import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import org.gradle.process.internal.ExecException
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import javax.inject.Inject

abstract class CommandLineWithOutputValue : ValueSource<String, CommandLineWithOutputValue.Parameters> {
    interface Parameters : ValueSourceParameters {
        val commands: Property<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        return try {
            executeCommand(parameters.commands.get())
        } catch (e: ExecException) {
            ""
        }
    }

    private fun executeCommand(command: String): String {
        val output = ByteArrayOutputStream()
        val error = ByteArrayOutputStream()
        execOperations.exec {
            try {
                commandLine("sh", "-c", command)
                standardOutput = output
                errorOutput = error
            } catch (e: Exception) {
            }
        }
        return String(output.toByteArray(), Charset.defaultCharset())
    }
}
