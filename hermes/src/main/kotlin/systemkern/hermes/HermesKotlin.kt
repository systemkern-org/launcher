package systemkern.hermes

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.logging.Level
import java.util.logging.Logger

@Component
class Hermes : TelegramLongPollingBot() {

    @Autowired
    private val nnImpl: NeuralNetworkImplementation? = null

    @Autowired
    private lateinit var userContextController: UserContextController

    override fun getBotToken(): String {
        return "692420469:AAE2ykxmFEpF9Kl9EveP051N_SVm2cTLuTI"
    }

    override fun getBotUsername(): String {
        return "hermes11Bot"
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            val message = update.message
            val response = SendMessage()
            response.setChatId(message.chatId!!)
            val responseText = nnImpl!!.generateAnswerToMessage(message.text)

            if (userContextController.checkUserId(message.from.id!!)) {
                userContextController.createStateForUser(message.from.id!!)
            } else {
                try {
                    userContextController.sendEvent(message.from.id!!,
                        nnImpl.contextTag!![0])
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

            }

            logger.log(Level.INFO, "Sent message: " + message.text)
            logger.log(Level.INFO, "Response: $responseText")
            response.text = responseText
            try {
                execute(response)
            } catch (e: TelegramApiException) {
                logger.warning("Failed to send message$responseText")
            }

        }
    }

    companion object {

        private val logger = Logger.getLogger(Hermes::class.java.name)
    }
}
