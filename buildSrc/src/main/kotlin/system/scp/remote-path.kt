package system.scp

data class RemotePath(
    val path: String,
    val host: String,
    val user: String? = null,
    val password: String? = null,
    val port: Int = 22,
) {
    companion object {
        fun builder() = Builder()
    }
    val hostSpec: String
        get() = if (user != null) "${user}@${host}" else host

    class Builder {
        private var path: String? = null
        private var host: String? = null
        private var user: String? = null
        private var password: String? = null
        private var port: Int = 22

        fun path(path: String) = apply { this.path = path }
        fun host(host: String) = apply { this.host = host }
        fun user(user: String) = apply { this.user = user }
        fun password(password: String) = apply { this.password = password }
        fun port(port: Int) = apply { this.port = port }
        fun build() = RemotePath(path!!, host!!, user, password, port)
    }
    fun toScpCommandPath() ="$hostSpec:$path"
}

fun String.toRemotePath(): RemotePath {
    require(startsWith("scp://")) { "Invalid remote path: $this. Does not start with scp://" }
    val (userAndPassword, hostAndPathParts) = splitIntoParts()
    val (host, path) = hostAndPathParts.extractHostAndPath()
    return remotePath(userAndPassword, path, host)
}

private fun remotePath(
    splitUserAndPassword: List<String>,
    path: String,
    host: String,
): RemotePath {
    return if (splitUserAndPassword.isEmpty()) {
        RemotePath(path, host)
    } else if (splitUserAndPassword.size == 1) {
        val (user) = splitUserAndPassword
        RemotePath(path, host, user)
    } else {
        val (user, password) = splitUserAndPassword
        RemotePath(path, host, user, password)
    }
}

private fun List<String>.extractHostAndPath(): Pair<String, String> {
    val (host) = take(1)
    val pathParts = drop(1)
    val path = "/" + pathParts.joinToString("/")
    return host.replace(":", "") to path
}

private fun String.splitIntoParts(): Pair<List<String>, List<String>> {
    val fullPath = substringAfter("scp://")
    val parts = fullPath.split("@")
    return if (parts.size < 2) {
        Pair(listOf(), parts.first().split("/"))
    } else {
        val (userAndPassword, hostAndPath) = parts
        val splitUserAndPassword = userAndPassword.split(":")
        val splitHostAndPath = hostAndPath.split("/")
        Pair(splitUserAndPassword, splitHostAndPath)
    }
}
