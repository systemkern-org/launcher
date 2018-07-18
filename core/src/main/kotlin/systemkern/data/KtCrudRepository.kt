package systemkern.data

import javassist.NotFoundException
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable
import java.util.*

@NoRepositoryBean
interface KtCrudRepository<T, ID> : CrudRepository<T, ID> {
    @Deprecated("\"Optional\" type is not useful in kotlin. Please use getById, or findById2 and the elvis operator ?:")
    override fun findById(var1: ID): Optional<T>
}

@Suppress("DEPRECATION")
fun <T, ID : Serializable> KtCrudRepository<T, ID>.findById2(id: ID): T? =
    findById(id).orElse(null)

// reifying this method makes it typesafe
inline fun <reified T, ID : Serializable> KtCrudRepository<T, ID>.getById(id: ID): T =
    findById2(id)
        ?: throw NotFoundException(T::class.java.simpleName + " with id: $id")
