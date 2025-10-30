import commands.SslReq
import commands.selfSigned
import kotlin.test.Test

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
        }
        println(req)
    }
}