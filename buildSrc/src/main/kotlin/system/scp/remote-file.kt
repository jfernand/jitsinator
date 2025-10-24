package system.scp

import commands.runCommand
import system.errln
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

val RECURSIVE = true
val NOT_RECURSIVE = false

fun getTempDirPath(): Path {
    return createTempDirectory("jitsinator") // Creates a new temp directory with a prefix
}

sealed interface RemoteFile {
    fun exists(): Boolean
    fun copyTo(newFile: RemoteFile, recursive: Boolean = NOT_RECURSIVE)
    fun moveTo(newFile: RemoteFile)
    fun isFolder(): Boolean
    fun remove(): Boolean
    fun toScpPath(): String
}

data class LocalFile(val path: String, val folder: File, val newName: String) : RemoteFile {
    override fun exists(): Boolean {
        return File(path).exists()
    }

    override fun copyTo(newFile: RemoteFile, recursive: Boolean) {
        TODO("Not yet implemented")
    }

    override fun moveTo(newFile: RemoteFile) {
        TODO("Not yet implemented")
    }

    override fun isFolder(): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(): Boolean {
        TODO("Not yet implemented")
    }

    override fun toScpPath(): String {
        TODO("Not yet implemented")
    }
}

data class ScpFile(val path: RemotePath) : RemoteFile {
    val workingDir: File = getTempDirPath().toFile()

    override fun exists(): Boolean {
        val tempFile = File(workingDir, "tempFile")

        return with(path) {
            runCatching {
                val cmd = "ssh -q -p $port $hostSpec 'test -e $path'"
                val ret = cmd.runCommand(workingDir)
                if (ret.exitValue() != 0) {
                    error("Error: $ret")
                }
            }.isSuccess
        }
    }



    override fun copyTo(newFile: RemoteFile, recursive: Boolean) {
        val recursive = if (recursive) "-r" else ""
        return with(path) {
            runCatching {
                val cmd = "scp $recursive -p -q -P $port ${toScpCommandPath()} ${newFile.toScpPath()}"
                val ret = cmd.runCommand(workingDir)
                if (ret.exitValue() != 0) {
                    error("Error: $ret")
                }
            }.isSuccess
        }
    }

    override fun moveTo(newFile: RemoteFile) {
        TODO("Not yet implemented")
    }

    override fun isFolder(): Boolean {
        // ssh user@host 'test -d -- /path/to/item'
        return with(path) {
            runCatching {
                val cmd =
                    "ssh -q -p $port $hostSpec 'test -d $path'"
                val ret = cmd.runCommand(workingDir)
                if (ret.exitValue() != 0) {
                    error("Error: $ret")
                }
            }.isSuccess
        }
    }

    override fun remove(): Boolean {
        // ssh -p 2222 user@host 'rm -f -- /path/to/file'
        return with(path) {
            runCatching {
                val cmd =
                    "ssh -q -p $port $hostSpec 'rm -f -- $path'"
                val ret = cmd.runCommand(workingDir)
                if (ret.exitValue() != 0) {
                    error("Error: $ret")
                }
            }.isSuccess
        }
    }

    override fun toScpPath(): String = path.toScpCommandPath()

    private val hostSpec: String
        get() = path.hostSpec
}
