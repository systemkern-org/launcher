package systemkern.profile

import org.springframework.stereotype.Component
import java.util.*

/** Helper class to create persisted test data for Unit Tests */
@Component
class UserProfileTestDataCreator internal constructor(
    private val repo: UserRepository
) {

    lateinit var userId: UUID

    /** Creates one User entity in the database and publishes the Id to UserRepository#userId */
    fun persistTestData() {
        userId = repo.save(createTestUser()).id
    }

}

internal fun createTestUser() =
    User(
        name = "Test User",
        password = "s3cret",
        username = "TestUser"
    )
