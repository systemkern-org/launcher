package systemkern

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi

@Component
class TelegramBotBean(){
    @Bean fun createBean() : TelegramBotsApi{
        ApiContextInitializer.init()
        // Instantiate Telegram Bots API
        val botsApi = TelegramBotsApi()
        try {
            botsApi.registerBot(HermesBot())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return botsApi
    }
}