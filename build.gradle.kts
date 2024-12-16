import commands.certificateManager
import commands.commander
import tasks.DockerComposeUp
import tasks.Dockerize

val workingFolderName = "jitsi-meet"

tasks.register<Dockerize>("dockerize") {
    group = "jitsi"
    workingFolderName = "jitsi-meet"
    rootDir.set(projectDir)
}

with(File(projectDir, workingFolderName)) {
    tasks.register<DockerComposeUp>("run") {
        group = "jitsi"
        workingDir.set(this@with)
    }

    with(commander(File(projectDir, workingFolderName))) {
        tasks.create("runWithJibri") {
            group = "jitsi"
            doLast {
                dockerComposeUp("docker-compose.yml", "jibri.yml")
                openWebpage("https://localhost:8443")
            }
        }

        tasks.create("runWithEtherpad") {
            group = "jitsi"
            doLast {
                dockerComposeUp("docker-compose.yml", "etherpad.yml")
                openWebpage("https://localhost:8443")
            }
        }
    }

    with(commander(projectDir)) {
        tasks.create("clean") {
            group = "jitsi"
            doLast {
                removeFromFs(buildFolder())
            }
        }
    }
}


with(certificateManager(File(projectDir, "certs"))) {
    tasks.create("generatePk") {
        group = "jitsi"
        doLast {
            generatePk()
        }
    }
    tasks.create("generateCsr") {
        group = "jitsi"
        doLast {
            generateCsr()
        }
    }
    tasks.create("generateSelfSignedCert") {
        group = "jitsi"
        doLast {
            generateSelfSignedCert()
        }
    }
}

fun Build_gradle.buildFolder() = layout.buildDirectory.get().asFile

