import org.junit.Assert.assertFalse
import system.scp.RECURSIVE
import system.scp.RemotePath
import system.scp.ScpFile
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScpFileTests {
    @Test
    fun testExists() {
        val p = RemotePath.builder()
            .path("/home/jav/test")
            .host("jitsi")
            .user("jav")
            .build()
        val f = ScpFile(p)
        assertTrue(f.exists())
    }

    @Test
    fun testExistsNot() {
        val p = RemotePath("/home/wqedqwed", "jitsi", "jav")
        val f = ScpFile(p)
        assertFalse(f.exists())
    }

    @Test
    fun testIsFolderTrue() {
        val cwd = System.getProperty("user.dir")
        val p = RemotePath(cwd, "localhost")
        val f = ScpFile(p)
        assertTrue(f.isFolder(), "$cwd is a folder")
    }

    @Test
    fun testIsFolderFalse() {
        val p = RemotePath("akawjhefkhewjhaevfjwaehvf", "localhost")
        val f = ScpFile(p)
        assertFalse(f.isFolder(), "$p is a folder")
    }

    @Test
    fun testCopy() {
        val tempDir = createTempDirectory()
        val cwd = System.getProperty("user.dir")
        val from = ScpFile(RemotePath("$cwd/src/main/resources/testfile", "localhost", "javier"))
        assertTrue(from.exists(), "Source fie exists in $cwd")
        val to = ScpFile(RemotePath("$tempDir/testfile", "localhost", "javier"))
        assertFalse(to.exists())
        from.copyTo(to)
        assertTrue(to.exists(), "Target file exists in $to")
        to.remove()
    }

    @Test
    fun testRecursiveCopy() {
        val tempDir = createTempDirectory()
        val cwd = System.getProperty("user.dir")
        val from = ScpFile(RemotePath("$cwd/src/main/resources/testFolder", "localhost", "javier"))
        assertTrue(from.exists(), "Source file exists in $cwd")
        val to = ScpFile(RemotePath("$tempDir/testFolder", "localhost", "javier"))
        assertFalse(to.exists())
        from.copyTo(to, RECURSIVE)
        assertTrue(to.exists(), "Target file exists in $to")
        assertTrue(to.isFolder(), "Target file exists $to is Folder")
        to.remove()
    }
}