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

fun InputStream.readText() = String(readAllBytes())

fun String.runCommand(workingDir: String) = runCommand(
    File(workingDir).absoluteFile
)

fun String.runCommand(workingDir: File) =
    run {
        errln("Running command: $this")
        ProcessBuilder("/bin/bash", "-c", this)
            .directory(workingDir)
            .start()
            .also {
                it.waitFor()
            }
    }

fun String.chomp() = replace("\n", "")
