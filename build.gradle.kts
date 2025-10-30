import commands.certificateManager
import commands.commander
import tasks.DockerComposeUp
import tasks.Dockerize
import tasks.DownloadTask

val workingFolderName = "jitsi-meet"
val whiteBoardDownloadUrl = "https://github.com/OnlineLearningSessions/whiteboard/archive/refs/heads/master.zip"

File(projectDir, workingFolderName).mkdirs()

tasks.register<Dockerize>("dockerize") {
    workingFolderName = "jitsi-meet"
    rootDir.set(projectDir)
}

with(File(projectDir, workingFolderName)) {
    tasks.register<DockerComposeUp>("run") {
        workingDir.set(this@with)
        dockerComposeFileNames.set(arrayOf("docker-compose.yml"))
    }

    tasks.register<DockerComposeUp>("runWithJibri") {
        workingDir.set(this@with)
        dockerComposeFileNames.set(arrayOf("docker-compose.yml", "jibri.yml"))
    }

    tasks.register<DockerComposeUp>("runWithEtherpad") {
        workingDir.set(this@with)
        dockerComposeFileNames.set(arrayOf("docker-compose.yml", "etherpad.yml"))
    }
}

with(commander(projectDir)) {
    tasks.create("clean") {
        group = "ols"
        doLast {
            removeFromFs(buildFolder())
        }
    }
}

with(certificateManager(File(projectDir, "certs"))) {
    tasks.create("generatePk") {
        group = "ols"
        doLast {
            generatePk()
//            generatePk(outFile = File("meet.jitsi.crt"))
        }
    }
    tasks.create("generateCsr") {
        group = "ols"
        doLast {
            generateCsr()
            generateCsr(key = File("meet.jitsi.key"), outFile = File("meet.jitsi.crt"))
        }
    }
    tasks.create("generateSelfSignedCert") {
        group = "ols"
        doLast {
            for (host  in listOf("localhost, meet.jitsi, auth.meet.jitsi"))
            generateSelfSignedCert(
                key = File("$host.key"),
                csr = File("$host.csr"),
                outFile = File("$host.crt")
            )
        }
    }
}

tasks.register<DownloadTask>("downloadWhiteboard") {
    rootDir.set(projectDir)
    downloadUrl.set(whiteBoardDownloadUrl)
    workingFolderName.set("whiteboard")
    destination.set("whiteboard-master.zip")
}

fun Project.buildFolder() = layout.buildDirectory.get().asFile


