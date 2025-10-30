package commands

import system.Os
import system.getCurrentOs
import java.io.File
import javax.security.auth.Subject

interface CertificateGenerator {
    val workingDir: File

    fun generatePk(outFile: File = File("localhost.key"))

    fun generateCsr(
        key: File = File("localhost.key"),
        outFile: File = File("localhost.csr"),
    )

    fun generateSelfSignedCert(
        csr: File = File("localhost.csr"),
        key: File = File("localhost.key"),
        outFile: File = File("localhost.crt"),
    )
}

fun certificateManager(workingDir: File): CertificateGenerator = when (getCurrentOs()) {
    Os.Linux -> LinuxCertificateGenerator(workingDir)
    Os.Mac -> TODO()
    is Os.Unknown -> TODO()
    Os.Windows -> TODO()
}

private class LinuxCertificateGenerator(override val workingDir: File) : CertificateGenerator {
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

    override fun generatePk(outFile: File) {
        pkCommand(outFile).runCommand(workingDir)
    }

    override fun generateCsr(key: File, outFile: File) {
        csrCommand(key, outFile).runCommand(workingDir)
    }

    override fun generateSelfSignedCert(csr: File, key: File, outFile: File) {
        selfSignedCertificateCommand(csr, key, outFile).runCommand(workingDir)
    }
}

sealed interface SslReq {
    enum class Type {
        Rsa, Ec
    }

    data class Plain(val name: String, val type: Type, val subject: Subj) : SslReq
    data class WithSan(val name: String, val type: Type, val subject: Subj, val san: San) : SslReq
    data class Subj(
        var c: String? = null,
        var st: String? = null,
        var l: String? = null,
        var o: String? = null,
        var ou: String? = null,
        var cn: String? = null,
    ) {
        override fun toString() : String = "/C=$c/ST=$st/L=$l/O=$o/OU=IT/CN=$cn"
    }

    data class San(val dnsNames: List<String>? = null, val ipAddresses: List<String>? = null)
}

fun main() {
    val req = selfSigned("localhost") {
        subj {
            c = "US"
            st = "MI"
            l = "Royal Oak"
            o = "onlinelearninsessions.com"
            ou = "org.cr"
            cn = "localhost"
        }
    }
    println(req)
}

class ReqCtx {
    lateinit var subj: SslReq.Subj
    var san: SslReq.San? = null
    var type: SslReq.Type? = null

    fun subj(block: SslReq.Subj.() -> Unit) {
        val s = SslReq.Subj()
        s.block()
        subj = s
    }

    fun toReq(name: String): SslReq = if (san == null) {
        SslReq.Plain(name, type ?: error("Encryption type is missing"), subj)
    } else {
        SslReq.WithSan(name, type ?: error("Encryption type is missing"), subj, san!!)
    }
}

fun selfSigned(name: String, block: ReqCtx.() -> Unit): SslReq {
    val ctx = ReqCtx()
    ctx.block()
    return ctx.toReq(name)
}