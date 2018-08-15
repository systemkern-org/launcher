package systemkern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Hermes extends TelegramLongPollingBot {

    private static final Logger logger = Logger.getLogger(Hermes.class.getName());

    @Value("${hermesToken}")
    private String token;

    @Value("${hermesBotName}")
    private String botName;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage response = new SendMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            response.setChatId(chatId);
            String responseText = "Sent message: " + message.getText();
            logger.log(Level.INFO,"Sent message: " + message.getText());
            response.setText("Sent: " + text);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                logger.warning("Failed to send message" + responseText);
            }
        }
    }
}
