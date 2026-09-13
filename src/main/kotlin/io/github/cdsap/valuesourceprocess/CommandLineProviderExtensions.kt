package io.github.cdsap.valuesourceprocess

import org.gradle.api.Project
import org.gradle.api.provider.Provider

fun Project.execute(command: String): Provider<String> {
    return providers.of(CommandLineWithOutputValue::class.java) {
        parameters.commands.set(command)
    }
}
