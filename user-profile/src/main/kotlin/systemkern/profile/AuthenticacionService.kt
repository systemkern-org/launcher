package systemkern.profile

import java.util.*
import kotlin.collections.HashMap

internal class AuthenticationService{

    companion object {
        @JvmStatic
        val tokens: HashMap<UUID, String> = HashMap()
    }
}
