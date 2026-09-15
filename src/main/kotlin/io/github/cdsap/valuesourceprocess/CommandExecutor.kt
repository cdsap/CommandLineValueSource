package io.github.cdsap.valuesourceprocess

import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

internal class CommandExecutor(
    private val execOperations: ExecOperations
) {
    fun execute(command: String): String {
        val output = ByteArrayOutputStream()
        val error = ByteArrayOutputStream()
        execOperations.exec {
            commandLine("sh", "-c", command)
            standardOutput = output
            errorOutput = error
        }
        return String(output.toByteArray(), Charset.defaultCharset())
    }
}
