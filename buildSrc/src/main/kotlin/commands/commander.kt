package commands

import io.github.oshai.kotlinlogging.KotlinLogging
import system.Os
import system.getCurrentOs
import java.io.File

interface Commander {
    val workingDir: File
    fun dockerComposeUp(vararg configFiles: String): Process
    fun getJitsiDownloadUrl(): Process
    fun download(url: String): Process
    fun generatePasswords(): Process
    fun removeFromFs(path: File): Process
    fun removeFromFs(path: String): Process
    fun unzip(pathSpec: String): Process
}

fun commander(workingDir: File) =
    when (val os = getCurrentOs()) {
        Os.Linux -> LinuxCommander(workingDir)
        Os.Mac -> error("Command Runner for MacOs not implemented yet")
        is Os.Unknown -> error("Command Runner for unknown os '${os.osName}' not implemented yet")
        Os.Windows -> TODO()
    }


class LinuxCommander(override val workingDir: File) : Commander {
    private val logger = KotlinLogging.logger {}

    override fun download(url: String) =
        "wget $url"
        .runCommand(workingDir)
        .also {
            logger.info { "wget $url" }
            }

    override fun removeFromFs(path: File) =
        "rm -Rf $path"
            .runCommand(workingDir)

    override fun removeFromFs(path: String) =
        "rm -Rf $path"
            .runCommand(workingDir)

    override fun dockerComposeUp(vararg configFiles: String) =
        configFiles
            .flatMap { listOf("-f", it) }
            .plus("up")
            .joinToString(" ").let {
                "sudo docker-compose $it -d".runCommand(workingDir)
            }

    override fun getJitsiDownloadUrl(): Process =
        "curl -s https://api.github.com/repos/jitsi/docker-jitsi-meet/releases/latest | grep 'zip' | cut -d\\\" -f4"
            .runCommand(workingDir)

    override fun unzip(pathSpec: String) =
        "unzip $pathSpec"
            .runCommand(workingDir)

    override fun generatePasswords() =
        "./gen-passwords.sh"
            .runCommand(workingDir)
}