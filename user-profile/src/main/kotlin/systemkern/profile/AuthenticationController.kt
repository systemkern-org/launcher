package systemkern.profile

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@RestController

internal class AuthenticationController(val service : AuthenticationService) {
    @PostMapping("/auth")
    internal fun login(auth : Authentication, @RequestHeader password : String)
        = service.authenticationProcess(auth, password)

    @DeleteMapping("auth/{id}")
    internal fun logout(@PathVariable id : UUID, request : HttpServletRequest) {
        service.deleteToken(id)
        request.session.invalidate()
    }
}

internal data class AuthenticationResponse(
    val token : UUID,
    val username : String,
    val userId : UUID,
    val validUntil : LocalDateTime
)

@ResponseStatus(NOT_FOUND)
internal class UserNotFoundException(message : String?) : RuntimeException(message)
