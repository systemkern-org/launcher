package systemkern.profile

import org.springframework.stereotype.Service
import java.util.UUID

@Service
internal class UserProfileService (
    val repository: UserProfileRepository
){
    internal fun findById(id: UUID)
        = repository.findById(id)

    internal fun save(userProfile: UserProfile) =
        repository.save(userProfile)

    internal fun findByUsername(username: String) =
        repository.findByUsername(username)
}