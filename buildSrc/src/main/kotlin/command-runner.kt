import java.io.File
import java.io.InputStream

fun Process.dump() =
    run {
        println("exit: ${this.exitValue()}")
        println("out: ${getOutput()}")
        println("err: ${getError()}")
        this
    }

fun Process.getOutput() =
    inputStream.readText().chomp()

fun Process.getError() = errorStream.readText().chomp()

fun InputStream.readText() = String(this.readAllBytes())

fun String.runCommand(workingDir: String) =
    runCommand(
        File(workingDir).absoluteFile
    )

val runCommand: String.(File) -> Process =
    when (currentOS) {
        Os.Linux -> LinuxCommandRunner()
        Os.Mac -> error("Command Runner for MacOs not implemented yet")
        is Os.Unknown -> error("Command Runner for unknown os '${currentOS.osName}' not implemented yet")
        Os.Windows -> WindowsCommandRunner()
    }

fun String.chomp() = replace("\n", "")

interface CommandRunner : (String, File) -> Process

class LinuxCommandRunner : CommandRunner {
    override fun invoke(command: String, workingDir: File): Process =
        command.run {
            errln("Running command: $this")
            ProcessBuilder("/bin/bash", "-c", this)
                .directory(workingDir)
                .start()
                .also {
                    it.waitFor()
                }.dump()
        }
}

class WindowsCommandRunner : CommandRunner {
    override fun invoke(p1: String, p2: File): Process =
        p1.run {
            errln("Running command: $this")
            ProcessBuilder("cmd", "/c", this)
                .directory(p2)
                .start()
                .also {
                    it.waitFor()
                }
        }
}