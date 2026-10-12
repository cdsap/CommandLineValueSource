package io.github.cdsap.valuesourceprocess

import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class CommandLineWithOutputValue : ValueSource<String, CommandLineWithOutputValue.Parameters> {
    interface Parameters : ValueSourceParameters {
        val command: Property<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        return CommandExecutor(execOperations).execute(parameters.command.get())
    }
}
