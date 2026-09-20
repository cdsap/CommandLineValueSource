package io.github.cdsap.valuesourceprocess

import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import org.gradle.process.internal.ExecException
import javax.inject.Inject

abstract class CommandLineWithOutputValue : ValueSource<String, CommandLineWithOutputValue.Parameters> {
    interface Parameters : ValueSourceParameters {
        val command: Property<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        return try {
            CommandExecutor(execOperations).execute(parameters.command.get())
        } catch (e: ExecException) {
            ""
        }
    }
}
