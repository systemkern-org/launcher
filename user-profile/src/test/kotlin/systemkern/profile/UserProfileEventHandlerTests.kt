package systemkern.profile

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest(classes = [
    UserProfileRepository::class,
    UserProfileRepository::class,
    UserProfileConfiguration::class,
    UserProfile::class
])
@EntityScan("systemkern")
@RunWith(SpringRunner::class)
@DataJpaTest(showSql = false)
@EnableJpaRepositories
internal class UserEventHandlerTest {

    @Autowired
    internal lateinit var repo: UserProfileRepository

    @Test internal fun `User Passwords are encrypted`() {
        val persistedUser = repo.save(
            UserProfile(
                username = "userTest",
                name = "Test User",
                password = "password"
            ))

        assertThat(persistedUser.password).isNotBlank()
        assertThat(persistedUser.password).isNotEqualTo("password")
    }

}
