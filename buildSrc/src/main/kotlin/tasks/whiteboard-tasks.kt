package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.InputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

abstract class DownloadTask : DefaultTask() {
    init {
        group = "ols"
        description = "Download a file"
    }

    @get:Input
    abstract val downloadUrl: Property<String>

    @get:Input
    abstract val workingFolderName: Property<String>

    @get:Input
    abstract val destination: Property<String>

    @get:InputDirectory
    abstract val rootDir: DirectoryProperty

    @TaskAction
    fun action() {
        downloadUrl(downloadUrl.get(), rootDir.asFile.get().toPath().resolve(destination.get()))
    }
}

fun downloadUrl(url: String, folder: Path) =
    URI(url).toURL().openStream().save(folder)

fun InputStream.save(target: Path) {
    Files.createDirectories(target.parent)
    use { src ->
        Files.newOutputStream(target).use { out ->
            val buf = ByteArray(8192)
            while (true) {
                val n = src.read(buf)
                if (n < 0) break
                out.write(buf, 0, n)
            }
        }
    }
}