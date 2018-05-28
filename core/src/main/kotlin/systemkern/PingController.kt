package systemkern

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/default/ping")
internal class PingController {

    @GetMapping
    fun ping(): PongDTO =
        PongDTO()

}

internal data class PongDTO(
    val timestamp: LocalDateTime = LocalDateTime.now()
)

