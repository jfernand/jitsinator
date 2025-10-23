package system.scp

import commands.runCommand
import system.copyTo
import system.errln
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

fun getTempDirPath(): Path {
    return createTempDirectory("jitsinator") // Creates a new temp directory with a prefix
}

sealed interface RemoteFile {
    fun exists() : Boolean
    fun renameTo(newFile: File) = newFile.copyTo(folder, newName)
    val folder: File
    val newName: String
}
data class LocalFile(val path:String, override val folder: File, override val newName: String) : RemoteFile {
    override fun exists(): Boolean {
        return File(path).exists()
    }
}

data class ScpFile(val host:String, val user:String, val password:String? = null, val path:String, val port:Int = 22,
                   override val folder: File,
                   override val newName: String
) : RemoteFile {
    val workingDir = getTempDirPath().toFile()
    override fun exists() : Boolean {
        val tempFile = File(workingDir, "tempFile")
        return runCatching {
            val cmd = "scp -p -q -P $port $user@$host:$path "
            if (password != null) {
                cmd.runCommand(File("/tmp"))
            } else {
//                cmd.runCommand(File("/tmp"), mapOf("SSH_ASKPASS" to "/"))
            }
        }.isSuccess
    }
}

fun ScpFile.moveTo(newLocation: RemoteFile): File {
    if (!exists()) {
        error("I can't move $this to $newLocation because i can't find it")
    }
    val file = File(folder, newName)
    errln("Moving $this ${exists()} to $file")
    renameTo(file)
    return file
}

fun ScpFile.copyTo(folder: File, newName: String): File {
    if (!exists()) {
        error("I can't move $this to $newName because i can't find it")
    }
    val file = File(folder, newName)
    errln("Moving $this ${exists()} to $file")
    copyTo(file, newName)
    return file
}

//infix fun ScpFile.sameAs(other: File) = checksum() == other.checksum()

//fun RemoteFile.checksum() = readText().hashCode()
