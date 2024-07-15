val workingFolderName = "jitsi-meet"

tasks.create("dockerize") {
    group = "jitsi"
    doLast {
        val name =
            "curl -s https://api.github.com/repos/jitsi/docker-jitsi-meet/releases/latest | grep 'zip' | cut -d\\\" -f4".runCommand(
                projectDir
            ).getOutput()
        val folder = downloadAndExtract(name, temporaryDir)
        errln("Extracted to $folder")
        val workingFolder = folder.moveTo(projectDir, workingFolderName)
        File(projectDir, "env.master")
            .moveTo(workingFolder, ".env")
        "./gen-passwords.sh".runCommand(workingFolder)
        "mkdir -p ~/.jitsi-meet-cfg/{web,transcripts,prosody/config,prosody/prosody-plugins-custom,jicofo,jvb,jigasi,jibri}".runCommand(
            workingFolder
        )
    }
}

tasks.create("run") {
    group = "jitsi"
    doLast {
        "docker-compose up -d".runCommand(File(projectDir, workingFolderName))
        openWebpage("https://localhost:8443")
    }
}

tasks.create("runWithJibri") {
    group = "jitsi"
    doLast {
        "docker-compose up -f docker-compose.yml -f jibri.yml".runCommand(File(projectDir, workingFolderName))
        openWebpage("https://localhost:8443")
    }
}

tasks.create("runWithEtherpad") {
    group = "jitsi"
    doLast {
        "docker-compose up -f docker-compose.yml -f etherpad.yml".runCommand(File(projectDir, workingFolderName))
        openWebpage("https://localhost:8443")
    }
}

tasks.create("clean") {
    group = "jitsi"
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
