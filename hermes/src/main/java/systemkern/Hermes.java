package systemkern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import systemkern.hermes.Events;
import systemkern.hermes.States;
import systemkern.hermes.UserContextController;
import systemkern.systemkern.hermes.NeuralNetworkImplementation;
import java.util.HashMap;
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

    @Autowired
    private NeuralNetworkImplementation nnImpl;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Autowired
    private UserContextController userContextController;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage response = new SendMessage();

            if (userContextController.checkUserId(message.getFrom().getId())){
                userContextController.createStateForUser(message.getFrom().getId());
            }

            response.setChatId(message.getChatId());
            String responseText = nnImpl.answerToMessage(message.getText());
            logger.log(Level.INFO,"Sent message: " + message.getText());
            logger.log(Level.INFO,"Response: " + responseText);
            response.setText(responseText);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                logger.warning("Failed to send message" + responseText);
            }
        }
    }
}
