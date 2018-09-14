package systemkern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import systemkern.hermes.Events;
import systemkern.hermes.UserContextController;
import systemkern.hermes.NeuralNetworkImplementation;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Hermes extends TelegramLongPollingBot {

    private static final Logger logger = Logger.getLogger(Hermes.class.getName());
    private HashMap<Integer,Events> events = new HashMap<>();

    public Hermes(){
        events.put(0, Events.RECEIVE_GREETING);
        events.put(1, Events.RECEIVE_GOODBYE );
        events.put(2, Events.RECEIVE_THANKS );
        events.put(3, Events.REQUEST_GENERAL_INFO );
        events.put(4, Events.REQUEST_GENERAL_INFO);
        events.put(5, Events.REQUEST_GENERAL_INFO);
        events.put(6, Events.REQUEST_GENERAL_INFO);
        events.put(7, Events.REQUEST_GENERAL_INFO);
        events.put(8, Events.REQUEST_RENT);
        events.put(9, Events.REQUEST_GENERAL_INFO);
    }

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
            response.setChatId(message.getChatId());
            String responseText = nnImpl.answerToMessage(message.getText());

            if (userContextController.checkUserId(message.getFrom().getId())){
                userContextController.createStateForUser(message.getFrom().getId());
            }else {
                try{
                    int tag = nnImpl.getContextTag()[0];
                    userContextController.sendEvent(message.getFrom().getId(), events.get(tag));
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
