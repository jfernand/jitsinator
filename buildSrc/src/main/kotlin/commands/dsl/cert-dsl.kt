package commands.dsl

sealed interface SslReq {
    enum class Type {
        Rsa, EdDsa, P256
    }

    data class Plain(val name: String, val type: Type, val subject: Subj, val keyout: String, val out: String) :
        SslReq {
        override fun toString(): String =
            when (type) {
                Type.Rsa -> "openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout $keyout -out $out -subj \"$subject\""
                Type.EdDsa -> "openssl req -x509 -nodes -days 365 -newkey ed25519:128 -keyout $keyout -out $out -subj \"$subject\""
                Type.P256 -> "openssl req -x509 -nodes -days 365 -newkey ec_param_gen:prime256v1 -keyout $keyout -out $out -subj \"$subject\""
            }
    }

    data class WithSan(
        val name: String,
        val type: Type,
        val subject: Subj,
        val san: San,
        val keyout: String,
        val out: String,
    ) : SslReq {
        override fun toString(): String {
            TODO("Not yet implemented")
        }
    }

    data class Subj(
        var c: String? = null,
        var st: String? = null,
        var l: String? = null,
        var o: String? = null,
        var ou: String? = null,
        var cn: String? = null,
    ) {
        override fun toString(): String = "/C=$c/ST=$st/L=$l/O=$o/OU=IT/CN=$cn"
    }

    data class San(val dnsNames: List<String>? = null, val ipAddresses: List<String>? = null)

}

data class ReqCtx(
    var san: SslReq.San? = null,
    var type: SslReq.Type? = null,
    var keyout: String = "certs/localhost.key",
    var out: String = "certs/localhost.crt",
) {
    lateinit var subj: SslReq.Subj

    fun subj(block: SslReq.Subj.() -> Unit) {
        val s = SslReq.Subj()
        s.block()
        subj = s
    }

    fun toReq(name: String): SslReq = if (san == null) {
        SslReq.Plain(name, type ?: error("Encryption type is missing"), subj, keyout!!, out!!)
    } else {
        SslReq.WithSan(name, type ?: error("Encryption type is missing"), subj, san!!, keyout!!, out!!)
    }
}

fun selfSigned(name: String, block: ReqCtx.() -> Unit): SslReq {
    val ctx = ReqCtx()
    ctx.block()
    return ctx.toReq(name)
}

fun main() {
    val req = selfSigned("localhost") {
        subj {
            c = "US"
            st = "MI"
            l = "Royal Oak"
            o = "onlinelearningsessions.com"
            ou = "org.cr"
            cn = "localhost"
        }
    }
    req.toString()
    println(req)
}