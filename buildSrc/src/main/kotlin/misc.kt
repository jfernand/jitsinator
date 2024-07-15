import java.net.URI
import java.awt.Desktop

fun openWebpage(url: String) {
    if (Desktop.isDesktopSupported()) {
        val desktop = Desktop.getDesktop()
        try {
            desktop.browse(URI(url))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        println("Desktop is not supported. Cannot open the URL.")
    }
}
