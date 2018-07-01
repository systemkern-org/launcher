package systemkern.profile

import org.springframework.stereotype.Service

@Service
internal class UserProfileService (val repo: UserProfileRepository){
    internal fun save(requestBody: UserProfile){
        repo.save(requestBody)
    }
}