package io.github.cdsap.valuesourceprocess

import org.gradle.process.ExecOperations
import org.gradle.process.internal.ExecException
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

internal class CommandExecutor(
    private val execOperations: ExecOperations
) {
    fun execute(command: String): String {
        val output = ByteArrayOutputStream()
        return try {
            execOperations.exec {
                commandLine("sh", "-c", command)
                standardOutput = output
            }
            String(output.toByteArray(), Charset.defaultCharset())
        } catch (e: ExecException) {
            ""
        }
    }
}
