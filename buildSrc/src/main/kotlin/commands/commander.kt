package commands

import system.Os
import system.getCurrentOs
import java.io.File

interface Commander {
    val workingDir: File
    fun download(url: String)
    fun removeFromFs(path: File)
    fun removeFromFs(path: String)
    fun runWithJibri()
    fun dockerComposeUp(vararg configFiles: String)
    fun runWithEtherpad()
    fun unzip(pathSpec: String)
}

fun commander(workingDir: File) =
    when (val os = getCurrentOs()) {
        Os.Linux -> LinuxCommander(workingDir)
        Os.Mac -> error("Command Runner for MacOs not implemented yet")
        is Os.Unknown -> error("Command Runner for unknown os '${os.osName}' not implemented yet")
        Os.Windows -> WindowsCommander(workingDir)
    }

class WindowsCommander(override val workingDir: File) : Commander {
    override fun download(url: String) {
        TODO("Not yet implemented")
    }

    override fun removeFromFs(path: File) {
        TODO("Not yet implemented")
    }

    override fun removeFromFs(path: String) {
        TODO("Not yet implemented")
    }

    override fun runWithJibri() {
        TODO("Not yet implemented")
    }

    override fun dockerComposeUp(vararg configFiles: String) {
        TODO("Not yet implemented")
    }

    override fun runWithEtherpad() {
        TODO("Not yet implemented")
    }

    override fun unzip(pathSpec: String) {
        TODO("Not yet implemented")
    }
}

class LinuxCommander(override val workingDir: File) : Commander {
    override fun download(url: String) {
        "wget $url".runCommand(workingDir)
    }

    override fun removeFromFs(path: File) {
        "rm -Rf $path".runCommand(workingDir)
    }

    override fun removeFromFs(path: String) {
        "rm -Rf $path".runCommand(workingDir)
    }

    override fun runWithJibri() {
        "sudo docker-compose -f docker-compose.yml -f jibri.yml up".runCommand(workingDir)
    }

    override fun dockerComposeUp(vararg configFiles: String) {
        val arguments = configFiles.flatMap { listOf("-f", it) }.plus("up").joinToString(" ")
        "sudo docker-compose $arguments -d".runCommand(workingDir)
    }

    override fun runWithEtherpad() {
        TODO("Not yet implemented")
    }

    override fun unzip(pathSpec: String) {
        "unzip $pathSpec".runCommand(workingDir)
    }
}