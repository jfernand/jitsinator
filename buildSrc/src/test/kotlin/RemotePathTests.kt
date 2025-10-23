import system.RemotePath
import system.toRemotePath
import kotlin.test.Test
import kotlin.test.assertEquals

class RemotePathTests {
    @Test
    fun testFullRemotePath() {
        val remotePath = RemotePath( "/path/to/remote/folder", "localhost", "user", password = "password")
        val s = "scp://user:password@localhost/path/to/remote/folder"
        assertEquals(remotePath, s.toRemotePath())
        println(remotePath)
    }

    @Test
    fun testRemotePathNoPassword() {
        val remotePath = RemotePath( "/path/to/remote/folder", "localhost", "user")
        val s = "scp://user@localhost/path/to/remote/folder"
        assertEquals(remotePath, s.toRemotePath())
        println(remotePath)
    }

    @Test
    fun testRemotePathNoUser() {
        val remotePath = RemotePath( "/path/to/remote/folder", "localhost")
        val s = "scp://localhost/path/to/remote/folder"
        assertEquals(remotePath, s.toRemotePath())
        println(remotePath)
    }
}