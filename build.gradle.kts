import commands.certificateManager
import commands.commander
import tasks.DockerComposeUp
import tasks.Dockerize

val workingFolderName = "jitsi-meet"

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
        group = "build setup"
        doLast {
            removeFromFs(buildFolder())
        }
    }
}

with(certificateManager(File(projectDir, "certs"))) {
    tasks.create("generatePk") {
        group = "dockerize"
        doLast {
            generatePk()
        }
    }
    tasks.create("generateCsr") {
        group = "dockerize"
        doLast {
            generateCsr()
        }
    }
    tasks.create("generateSelfSignedCert") {
        group = "dockerize"
        doLast {
            generateSelfSignedCert()
        }
    }
}

fun Build_gradle.buildFolder() = layout.buildDirectory.get().asFile

