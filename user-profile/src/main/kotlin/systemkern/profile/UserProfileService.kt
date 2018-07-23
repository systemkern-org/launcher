package systemkern.profile

import org.springframework.stereotype.Service
import java.util.*

@Service
internal class UserProfileService(
    private val repo: UserProfileRepository
) {
    internal fun save(requestBody: UserProfile) =
        repo.save(requestBody)

    internal fun findById(id: UUID)
        = repo.findById(id)


    internal fun update(updateRequest: RequestedDataClass,id: UUID)
        = save(mapAttributes(repo.findById(id).get(),updateRequest))

    internal fun mapFromNewUser(updateRequest: RequestedDataClass)
        = UserProfile(
        name = updateRequest.name,
        password = updateRequest.password,
        username = updateRequest.username,
        email = updateRequest.email
    )

    internal fun mapAttributes(userProfile:UserProfile, updateRequest: RequestedDataClass) : UserProfile {
        userProfile.name = updateRequest.name
        userProfile.password = updateRequest.password
        userProfile.username = updateRequest.username
        userProfile.email = updateRequest.email

        return userProfile
    }
}