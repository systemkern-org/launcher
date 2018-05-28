package systemkern.profile

import org.springframework.stereotype.Component
import java.util.*

@Component
class UserProfileTestDataCreator internal constructor(
    private val repo: UsersRepository
) {

    companion object {
        lateinit var userId: UUID
    }

    fun persistTestData() {
        userId = repo.save(createTestUser()).id
    }

}

internal fun createTestUser() =
    User(
        name = "Test User",
        password = "s3cret"
    )
