import java.io.File

fun errln(s: Any) = System.err.println(s)

fun File.moveTo(folder: File, newName: String): File {
    val file = File(folder, newName)
    errln("Moving $this ${exists()} to ${file}")
    renameTo(file)
    return file
}