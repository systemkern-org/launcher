package systemkern.data

import javassist.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestComponent
import org.springframework.data.jpa.domain.AbstractPersistable
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Repository
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import java.util.UUID.randomUUID
import javax.persistence.Entity


@SpringBootTest(classes = [KtCrudRepository::class])
@EntityScan("systemkern.data")
@RunWith(SpringRunner::class)
@DataJpaTest(showSql = false)
@EnableJpaRepositories
internal class KtCrudRepositoryIT {

    @Autowired
    private lateinit var repo: TestRepo

    @Test fun `Can get entity with id`() {
        val id = repo.save(createTestEntity()).id!!
        assertThat(id).isNotNull()

        val ret = repo.getById(id)
        assertThat(ret).isNotNull
    }

    @Test(expected = NotFoundException::class)
    fun `Throws exception on get entity with nonexistent id`() {
        repo.getById(UUID.randomUUID())
    }

    @Test fun `Can find entity with typed method findById2`() {
        val id = repo.save(createTestEntity()).id!!
        assertThat(id).isNotNull()

        val ret = repo.findById2(id)
        assertThat(ret).isNotNull
    }

    @Test fun `Can receive null for nonexistent id`() {
        val ret = repo.findById2(UUID.randomUUID())
        assertThat(ret).isNull()
    }

    @Test fun `Can findAll entities`() {
        repo.save(createTestEntity())
        repo.save(createTestEntity())
        val ret = repo.findAll()

        assertThat(ret).isNotNull
        assertThat(ret).isNotEmpty
        assertThat(ret).size().isEqualTo(2)
    }

    @Test(expected = NotFoundException::class)
    fun `Exception on getById for nonexistent id`() {
        repo.getById(randomUUID())
    }

}

private fun createTestEntity() =
    TestEntity(
        field1 = "foo",
        field2 = 42
    )

@Entity
private data class TestEntity(
    val field1: String,
    val field2: Int
) : AbstractPersistable<UUID>()

@TestComponent
@Repository
private interface TestRepo : KtCrudRepository<TestEntity, UUID>
