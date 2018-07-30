package systemkern.profile

import org.springframework.stereotype.Service

@Service
internal class UserProfileService (
    val repository: UserProfileRepository
){

    internal fun save(userProfile: UserProfile) =
        repository.save(userProfile)

    internal fun findByUsername(username: String) =
        repository.findByUsername(username)
}