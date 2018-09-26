package systemkern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import systemkern.hermes.UserContextController;
import systemkern.hermes.NeuralNetworkImplementation;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Hermes extends TelegramLongPollingBot {

    private static final Logger logger = Logger.getLogger(Hermes.class.getName());

    @Override
    public String getBotToken() {
        return "{hermesToken}";
    }

    @Override
    public String getBotUsername() {
        return "hermes11Bot";
    }

    @Autowired
    private NeuralNetworkImplementation nnImpl;

    @Autowired
    private UserContextController userContextController;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            SendMessage response = new SendMessage();
            response.setChatId(message.getChatId());
            String responseText = nnImpl.generateAnswerToMessage(message.getText());

            if (userContextController.checkUserId(message.getFrom().getId())){
                userContextController.createStateForUser(message.getFrom().getId());
            }else {
                try{
                    userContextController.sendEvent(message.getFrom().getId(),
                            nnImpl.getContextTag()[0]);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

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
