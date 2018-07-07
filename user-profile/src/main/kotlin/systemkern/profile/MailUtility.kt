package systemkern.profile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.util.*

@Component
internal class MailUtility(@Autowired
                           private val emailSender: JavaMailSender
) {
    internal var urlToVerify: String = ""
    internal val message: SimpleMailMessage = SimpleMailMessage()

    internal fun createEmailMessage(emailAddress: String,
                                    tokenId: UUID,
                                    baseUrl: String,
                                    subject: String
    ){
        message.setTo(emailAddress)
        message.subject = subject
        urlToVerify = buildLink(tokenId,baseUrl)
        message.text = urlToVerify
    }

    private fun buildLink(tokenId: UUID,baseUrl: String): String {

        var url = "http://"
        url += InetAddress.getLocalHost().hostAddress
        url += ":8080"
        url += baseUrl + tokenId.toString()
        return url

    }

    internal fun sendMessage() =
        emailSender.send(message)
}