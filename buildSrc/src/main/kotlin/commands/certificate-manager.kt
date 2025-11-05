package commands

import commands.dsl.SslReq
import commands.dsl.selfSigned
import io.github.oshai.kotlinlogging.KotlinLogging
import system.Os
import system.getCurrentOs
import java.io.File

interface CertificateGenerator {
    val workingDir: File

//    fun generatePk(outFile: File = File("localhost.key"))
//
//    fun generateCsr(
//        key: File = File("localhost.key"),
//        outFile: File = File("localhost.csr"),
//    )
//
//    fun generateSelfSignedCert(
//        csr: File = File("localhost.csr"),
//        key: File = File("localhost.key"),
//        outFile: File = File("localhost.crt"),
//    )

    fun generateSelfSignedCert(
        sslReq: SslReq,
    ): Result<Unit, CmdError>
}

fun certificateManager(workingDir: File): CertificateGenerator = when (getCurrentOs()) {
    Os.Linux -> LinuxCertificateGenerator(workingDir)
    Os.Mac -> TODO()
    is Os.Unknown -> TODO()
    Os.Windows -> TODO()
}

private class LinuxCertificateGenerator(override val workingDir: File) : CertificateGenerator {
    private val logger = KotlinLogging.logger {}

    fun generate(req: SslReq) =
        req.toString()
            .also { logger.info { "Generating certificate with command: $it" } }

    fun pkCommand(outFile: File) =
        "openssl genrsa -out ${File(workingDir, outFile.name)} 2048"

    fun csrCommand(key: File, outFile: File) =
        "openssl req -new -key ${File(workingDir, key.name)} -out ${File(workingDir, outFile.name)}"

    fun selfSignedCertificateCommand(csr: File, key: File, outFile: File) =
        "openssl x509 -req -days 365 -in ${File(workingDir, csr.name)} -signkey ${
            File(
                workingDir,
                key.name
            )
        } -out ${File(workingDir, outFile.name)} "

    fun selfSignedCertificateCommandWithSan(csr: File, key: File, outFile: File) =
        //openssl req -new -newkey rsa:2048 -nodes -keyout server.key -out server.csr -config san.cnf
        "openssl x509 -req -days 365 -in ${File(workingDir, csr.name)} -signkey ${
            File(
                workingDir,
                key.name
            )
        } -out ${File(workingDir, outFile.name)} "

//    override fun generatePk(outFile: File) {
//        pkCommand(outFile).runCommand(workingDir)
//    }
//
//    override fun generateCsr(key: File, outFile: File) {
//        csrCommand(key, outFile).runCommand(workingDir)
//    }
//
//    override fun generateSelfSignedCert(csr: File, key: File, outFile: File) {
//        selfSignedCertificateCommand(csr, key, outFile).runCommand(workingDir)
//    }

    override fun generateSelfSignedCert(sslReq: SslReq): Result<Unit, CmdError> {
        val cmd = "$sslReq"
        val process = cmd.runCommand(workingDir)
        return when (val ret = process.waitFor()) {
            0 -> Result.Success(Unit)
            else -> Result.Failure(CmdError(ret, process.getOutput(), process.getError()))
        }
    }
}

data class CmdError(val exitCode: Int, val output: String, val stdError: String)

sealed interface Result<T, E> {
    data class Success<T, E>(val output: T) : Result<T, E>
    data class Failure<T, E>(val output: E) : Result<T, E>
}