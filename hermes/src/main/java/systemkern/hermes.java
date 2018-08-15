package systemkern;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class hermes extends TelegramLongPollingBot {

    private static final Logger logger = Logger.getLogger(hermes.class.getName());

  /*  @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;*/

    @Override
    public String getBotToken() {
        return "692420469:AAE2ykxmFEpF9Kl9EveP051N_SVm2cTLuTI";
    }

    @Override
    public String getBotUsername() {
        return "hermes11Bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage response = new SendMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            response.setChatId(chatId);
            String responseText = "Mensaje enviado: " + message.getText();
            logger.log(Level.INFO,"Mensaje enviado: " + message.getText());
            response.setText("Sent: " + text);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                logger.warning("Failed to send message" + responseText);
            }
        }
    }

   /* @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", username, token);
    }*/

}
