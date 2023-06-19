# CommandLineWithOutputValue
Library providing the ValueSource `CommandLineWithOutputValue`. This component will execute an external
command and will return the output. The usage of ValueSources keeps compatibility with Configuration Cache.
Examples usage in repositories:
* Info Kotlin Process https://github.com/cdsap/InfoKotlinProcess

## Dependency
```
dependencies {
    implementation("io.github.cdsap:jdk-tools-parser:0.1.0")
}
```

## Usage

```
val commnandExample = "jps | grep KotlinCompileDaemon | sed 's/KotlinCompileDaemon//' | while read ln; do  jstat -gc -t \$ln; echo \"\$ln\"; done"
val example = providers.of(CommandLineWithOutputValue::class.java) {
     parameters.commands.set("jps")
}.get()
```
In this simple use case the value example is:
```
Timestamp           S0C         S1C         S0U         S1U          EC           EU           OC           OU          MC         MU       CCSC      CCSU     YGC     YGCT     FGC    FGCT     CGC    CGCT       GCT
         4192.9         0.0         0.0         0.0         0.0      36864.0          0.0      55296.0      23061.6    68800.0    67735.0    8064.0    7685.5     24     0.093     1     0.035    10     0.007     0.135
23906

```
