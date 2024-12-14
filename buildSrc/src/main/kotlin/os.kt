import org.apache.tools.ant.taskdefs.condition.Os.*

sealed interface Os {
    object Windows: Os
    object Mac: Os
    object Linux: Os
    data class Unknown(val osName:String): Os
}

val currentOS = when {
    isFamily(FAMILY_WINDOWS) -> Os.Windows
    isFamily(FAMILY_MAC) -> Os.Mac
    isFamily(FAMILY_UNIX) -> Os.Linux
    else -> Os.Unknown(System.getProperty("os.name"))
}
