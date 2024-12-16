package tasks

import commands.Commander
import commands.LinuxCommander
import commands.commander
import commands.getOutput
import commands.runCommand
import errln
import moveTo
import copyTo
import openWebpage
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class DockerComposeUp : DefaultTask() {
    @get:Input
    val commander: Commander by lazy { commander(workingDir.asFile.get()) }

    @get:InputDirectory
    abstract val workingDir: DirectoryProperty

    init {
        group = "jitsi"
        description = "sample task"
    }

    @TaskAction
    fun action() {
        commander.dockerComposeUp("docker-compose.yml") // TODO accept vars
        openWebpage("https://localhost:8443")
    }
}

abstract class Dockerize: DefaultTask() {
    @get:Input
    abstract val workingFolderName: Property<String>

    @get:InputDirectory
    abstract val rootDir: DirectoryProperty

    @TaskAction
    fun action() {
        val process =
            "curl -s https://api.github.com/repos/jitsi/docker-jitsi-meet/releases/latest | grep 'zip' | cut -d\\\" -f4".runCommand(
                rootDir.asFile.get().absolutePath
            )
        val zipFile = process.getOutput()
        errln("Downloading and extracting from: |$zipFile|")

        val folder = downloadAndExtract(zipFile, temporaryDir)
        errln("Extracted to $folder")

        val workingFolder = folder.moveTo(rootDir.asFile.get(), workingFolderName.get())
        errln("$folder moved to $workingFolder")

        File(rootDir.asFile.get(), "env.master")
            .copyTo(workingFolder, ".env")
        errln(".env file copied to $workingFolder")

        errln("Generating passwords to .env file in $workingFolder")
        "./gen-passwords.sh".runCommand(workingFolder)

        errln("Generating jitsi config files in ~!??!?!")
        "mkdir -p ~/.jitsi-meet-cfg/{web,transcripts,prosody/config,prosody/prosody-plugins-custom,jicofo,jvb,jigasi,jibri}".runCommand(
            workingFolder
        )
    }
}

private fun downloadAndExtract(name: String, tempDir: File) =
    with(commander(tempDir)) {
        errln("Downloading $name")
        removeFromFs("stable*")
        download(name)
        unzip("*")
        tempDir.listFiles()
            ?.also { errln("Found ${it.size} files in $tempDir") }
            ?.filter { it.isDirectory }
            ?.first { it.name.startsWith("jitsi-docker-jitsi-meet-") }
            ?: error("Could not find jitsi folder in $tempDir")
    }