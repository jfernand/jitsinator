
tasks.create("dockerize") {
    group = "build-jitsi"
    doLast {
        val name =
            "curl -s https://api.github.com/repos/jitsi/docker-jitsi-meet/releases/latest | grep 'zip' | cut -d\\\" -f4".runCommand(
                projectDir
            ).getOutput()
        val folder = downloadAndExtract(name, temporaryDir)
        errln("Extracted to $folder")
        val workingFolder = folder.moveTo(projectDir, "jitsi-meet")
        File(projectDir, "env.master")
            .moveTo(workingFolder, ".env")
        "./gen-passwords.sh".runCommand(workingFolder)
        "mkdir -p ~/.jitsi-meet-cfg/{web,transcripts,prosody/config,prosody/prosody-plugins-custom,jicofo,jvb,jigasi,jibri}".runCommand(workingFolder)
    }
}

tasks.create("clean") {
    group = "tasks"
    doLast {
        "rm -Rf ${buildFolder()}".runCommand(projectDir)
    }
}

fun errln(s: Any) = System.err.println(s)

fun downloadAndExtract(name: String, tempDir: File): File {
    errln("Downloading $name")
    "rm -Rf stable*".runCommand(tempDir)
    "wget $name".runCommand(tempDir)
    "unzip *".runCommand(tempDir)
    return tempDir.listFiles()
        ?.filter { it.isDirectory }
        ?.first { it.name.startsWith("jitsi-docker-jitsi-meet-") } ?: error("Could not find jitsi folder in $tempDir")
}

fun Build_gradle.buildFolder() = layout.buildDirectory.get().asFile
