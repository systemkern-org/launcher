package systemkern.profile

import org.springframework.stereotype.Component
import java.util.*

/** Helper class to create persisted test data for Unit Tests */
@Component
class UserProfileTestDataCreator internal constructor(
    private val repo: UserProfileRepository
) {

    lateinit var userId: UUID

    /** Creates one UserProfile entity in the database and publishes the Id to UserProfileRepository#userId */
    fun persistTestData() {
        userId = repo.save(createTestUser()).id_userProfile
    }

}

internal fun createTestUser() =
    UserProfile(
        username = "userTest",
        name = "Test User",
        password = "s3cret",
        email = "userTest@gmail.com"
    )
