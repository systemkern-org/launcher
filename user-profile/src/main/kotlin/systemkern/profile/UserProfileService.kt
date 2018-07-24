package systemkern.profile

import org.springframework.stereotype.Service
import java.util.*

@Service
internal class UserProfileService(
    private val repo: UserProfileRepository
) {
    internal fun save(requestBody: UserProfile) =
        repo.save(requestBody)
}