import commands.CmdError
import commands.Result
import commands.certificateManager
import commands.dsl.SslReq
import commands.dsl.selfSigned
import java.io.File
import kotlin.test.Test
import kotlin.test.fail

class CertGenTests {
    @Test
    fun testSelfSigned() {
        val req = selfSigned("localhost") {
            type = SslReq.Type.Rsa
            subj {
                c = "US"
                st = "MI"
                l = "Royal Oak"
                o = "onlinelearninsessions.com"
                ou = "org.cr"
                cn = "localhost"
            }
            keyout = "/tmp/localhost.key"
            out = "/tmp/localhost.crt"
        }
        val commander = certificateManager(File("/tmp"))
        when (val ret = commander.generateSelfSignedCert(req)) {
            is Result.Failure<*, CmdError> -> {
                println(ret.output.output)
                println(ret.output.stdError)
                fail("Process executes correctly")
            }

            is Result.Success<*, *> -> {}
        }
    }
}