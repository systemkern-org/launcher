package systemkern.profile

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.util.*

@Component
internal class MailUtility(
    private val emailSender: JavaMailSender,
    internal var urlToVerify: String = "",
    internal val message: SimpleMailMessage = SimpleMailMessage()
) {
  
    internal fun createEmailMessage(
        emailAddress: String,
        tokenId: UUID,
        baseUrl: String,
        subject: String
    ) {
        message.setTo(emailAddress)
        message.subject = subject
        buildLink(tokenId, baseUrl)
        message.text = urlToVerify
    }

    private fun buildLink(
        tokenId: UUID,
        baseUrl: String
    ) {
        urlToVerify = ""
        urlToVerify += "http://"
        urlToVerify += InetAddress.getLocalHost().hostAddress
        urlToVerify += ":8080"
        urlToVerify += baseUrl + tokenId.toString()
    }

    internal fun sendMessage() =
        emailSender.send(message)
}