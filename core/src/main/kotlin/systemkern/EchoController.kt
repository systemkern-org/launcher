package systemkern


import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.validation.Valid

@RestController
@RequestMapping("/default/echo")
internal class EchoController {

    @PostMapping
    fun echo(@Valid @RequestBody value: EchoDTO) =
        value

}

internal data class EchoDTO(
    val id: Int,
    val value: String,
    val timestamp: LocalDateTime? = LocalDateTime.now()
)

