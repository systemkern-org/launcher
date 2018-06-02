package systemkern.profile

import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service

internal class AuthenticationService {

    companion object {
        @JvmStatic
        val tokens: HashMap<UUID,String> = HashMap()
    }
}

