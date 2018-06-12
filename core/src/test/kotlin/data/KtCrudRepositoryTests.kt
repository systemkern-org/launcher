package systemkern.data

import javassist.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.domain.AbstractPersistable
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Repository
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import java.util.UUID.*
import javax.persistence.Entity


@SpringBootTest(classes = [KtCrudRepository::class])
@EntityScan("systemkern")
@RunWith(SpringRunner::class)
@DataJpaTest(showSql = false)
@EnableJpaRepositories
internal class KtCrudRepositoryTest {

    @Autowired
    private lateinit var repo: TestRepo

    @Test fun `Can get entity with id`() {
        val id = repo.save(getTestEntity()).id!!
        assertThat(id).isNotNull()

        val ret = repo.getById(id)
        assertThat(ret).isNotNull
    }

    @Test fun `Can find entity with typed method findById2`() {
        val id = repo.save(getTestEntity()).id!!
        assertThat(id).isNotNull()

        val ret = repo.findById2(id)
        assertThat(ret).isNotNull
    }
    @Test fun `Can findAll entities`() {
        repo.save(getTestEntity())
        repo.save(getTestEntity())
        val ret = repo.findAll()

        assertThat(ret).isNotNull
        assertThat(ret).isNotEmpty
        assertThat(ret).size().isEqualTo(2)
    }

    @Test(expected = NotFoundException::class)
    fun `Exception on getById for nonexistent id`() {
        //repo is empty
        val ret = repo.getById(randomUUID())
    }

}

private fun getTestEntity() =
    TestEntity(
        field1 = "foo",
        field2 = 42
    )

@Entity
private data class TestEntity(
    val field1: String,
    val field2: Int
) : AbstractPersistable<UUID>()

@Repository
private interface TestRepo : KtCrudRepository<TestEntity, UUID>
