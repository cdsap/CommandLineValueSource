package io.github.cdsap.valuesourceprocess

import org.gradle.api.Project
import org.gradle.api.provider.Provider

fun Project.jStat(processName: String): Provider<String> {
    return execute("jps | grep $processName | sed 's/$processName//' | while read ln; do  jstat -gc -t \$ln; echo \"\$ln\"; done")
}

fun Project.jInfo(processName: String): Provider<String> {
    return execute("jps | grep $processName | sed 's/$processName//' | while read ln; do  jinfo \$ln  | grep \"XX:MaxHeapSize\"; echo \"\$ln\";  done")
}

fun Project.execute(command: String): Provider<String> {
    return providers.of(CommandLineWithOutputValue::class.java) {
        parameters.commands.set(command)
    }
}
