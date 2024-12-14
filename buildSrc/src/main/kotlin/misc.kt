import java.net.URI
import java.awt.Desktop

fun openWebpage(url: String) {
    if (Desktop.isDesktopSupported()) {
        val desktop = Desktop.getDesktop()
        try {
            if (desktop.isSupported(Desktop.Action.BROWSE))
                desktop.browse(URI(url))
            else
                println("Desktop does not support BROWSE action.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        println("Desktop is not supported. Cannot open the URL.")
    }
}
