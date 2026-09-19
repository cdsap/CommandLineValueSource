package io.github.cdsap.valuesourceprocess

import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

fun ProviderFactory.execute(command: String): Provider<String> {
    return of(CommandLineWithOutputValue::class.java) {
        parameters.commands.set(command)
    }
}

fun ProviderFactory.commandOutput(command: String): Provider<String> {
    return execute(command)
}
