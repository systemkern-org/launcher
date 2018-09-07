package systemkern.systemkern.hermes

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import systemkern.NaturalLanguagePreProcessor

@Configuration
@ConfigurationProperties("hermes")
internal class HermesConfiguration(
    val nlpProcessor : NaturalLanguagePreProcessor = NaturalLanguagePreProcessor(),
    val numberOfEpochsRequiredForTraining : Int = 100) {

    @Bean
    fun loadAndTrainModel() : NeuralNetworkImplementation{

        nlpProcessor.loadJsonFile(
            "D:/Documentos/proyectosFreelance/SystemKern-launcher/" +
                "real/launcher/hermes/src/main/resources/trainin_doc.txt")

        nlpProcessor.tokenizeWordsInIntents()
        val nnImpl = NeuralNetworkImplementation(
            numberOfEpochsRequiredForTraining,
            nlpProcessor)

        nnImpl.trainMNN()
        return nnImpl

    }

}
