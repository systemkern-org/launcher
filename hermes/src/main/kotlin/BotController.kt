import io.github.openunirest.http.Unirest
import io.github.openunirest.http.exceptions.UnirestException
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Level
import java.util.logging.Logger

@RestController("/bot")
class ComunicationController(
    private val API_ENDPOINT : String = "https://api.telegram.org/bot",
    private val START_COMMAND : String = "/start",
    private val ECHO_COMMAND : String = "/reply",
    private val logger: Logger = Logger.getLogger("[Hermes]")
){

    @Value("\${token}")
    lateinit var token : String

    @PostMapping("/\${token}")
    fun update(@RequestBody message : Message){
        when {
            message.content.startsWith(START_COMMAND) -> interact(message.id,message.content,"start")
            message.content.startsWith(ECHO_COMMAND) -> reply(message.id, message.content)
        }
    }

    fun reply(chatId : Long, text : String) {
        interact(chatId,
                 text.subSequence(
                     ECHO_COMMAND.length,
                     text.length)
                     .trim()
                     .toString(),
            "reply")
    }

    fun interact(chatId: Long, text: String, action: String){
        try {
            sendMessage(chatId, text)
        } catch (e : UnirestException) {
            logger.log(Level.SEVERE, "Can not send perform $action!", e)
        }
    }

    @Throws(UnirestException::class)
    private fun sendMessage(chatId: Long, text: String) {
        Unirest.post("$API_ENDPOINT$token/sendMessage")
            .field("chat_id", chatId)
            .field("text", text)
            .asJson()
    }
}

data class Message(
    val id: Long,
    val content: String
)
