import commands.certificateManager
import commands.commander
import commands.dsl.SslReq
import commands.dsl.selfSigned
import tasks.DockerComposeUp
import tasks.Dockerize
import tasks.DownloadTask

val workingFolderName = "jitsi-meet"
val whiteBoardDownloadUrl = "https://github.com/OnlineLearningSessions/whiteboard/archive/refs/heads/master.zip"

File(projectDir, workingFolderName).mkdirs()

tasks.register<Dockerize>("dockerize") {
    dependsOn("generateSelfSignedCert")
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
    tasks.create<Task>("clean") {
        group = "ols"
        doLast {
            removeFromFs(buildFolder())
        }
    }
}

with(certificateManager(File(projectDir, "certs"))) {
//    tasks.create("generatePk") {
//        group = "ols"
//        doLast {
//            generatePk()
////            generatePk(outFile = File("meet.jitsi.crt"))
//        }
//    }
//    tasks.create("generateCsr") {
//        group = "ols"
//        doLast {
//            generateCsr()
//            generateCsr(key = File("meet.jitsi.key"), outFile = File("meet.jitsi.crt"))
//        }
//    }
    tasks.create<Task>("generateSelfSignedCert") {
        group = "ols"
        description = "Generates self-signed certificate to be used by e.g. prosody"
        doLast {
            var req: SslReq.Plain = selfSigned("localhost") {
                type = SslReq.Type.Rsa
                subj {
                    c = "US"
                    st = "MI"
                    l = "Royal Oak"
                    o = "onlinelearninsessions.com"
                    ou = "org.cr"
                    cn = "localhost"
                }
            } as SslReq.Plain
            for (host in listOf("localhost", "meet.jitsi", "auth.meet.jitsi")) {
                req = req.copy(name = host, out = "$host.crt", keyout = "$host.key")
                generateSelfSignedCert(req)
            }
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


