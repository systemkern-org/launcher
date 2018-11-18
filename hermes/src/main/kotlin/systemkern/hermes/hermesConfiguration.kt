package systemkern.hermes

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.InputStream

@Configuration
@ConfigurationProperties("hermes")
internal class HermesConfiguration(
    private val numberOfEpochsRequiredForTraining: Int = 100,
    private var pathToTrainedModelString: String? = null,
    private var nnImpl: NeuralNetworkImplementation? = null,
    private var inputStream: InputStream? = null,
    private val fileName: String = "trained_model.zip",
    private val pathSeparator: String = System.getProperty("file.separator")) {

    @Autowired
    internal lateinit var nlpProcessor: NaturalLanguagePreProcessor
    private lateinit var pathToJson: String
    private lateinit var pathElements : ArrayList<String>

        @Bean
    fun loadAndTrainModel(): NeuralNetworkImplementation {
        initializeConstants()
        nnImpl = NeuralNetworkImplementation(numberOfEpochsRequiredForTraining)
        nlpProcessor.loadJsonFile(pathToJson)
        nnImpl!!.nlpProcessor = nlpProcessor

        try {
            if (!nlpProcessor.loadDictionary()) {
                inputStream = File(pathToTrainedModelString + pathSeparator + fileName).inputStream()
                nnImpl!!.loadModel(pathToTrainedModelString + pathSeparator + fileName)
            } else {
                throw NotLoadedDictionaryException()
            }
        } catch (e: Exception) {
            nlpProcessor.tokenizeWordsInIntents()
            nnImpl!!.configureModel()
            nnImpl!!.trainMNN()
        }
        return nnImpl as NeuralNetworkImplementation
    }

    fun initializeConstants() {
        pathToJson = System.getProperty("user.dir")
        if(pathToJson.contains("hermes")){
            pathElements = arrayListOf("src","main","resources")
        } else {
            pathElements = arrayListOf("hermes","src","main","resources")
        }
        for(element in pathElements){
            pathToJson = pathToJson.plus(pathSeparator)
            pathToJson = pathToJson.plus(element)
        }
        pathToJson = pathToJson.plus(pathSeparator + "training_doc.txt")
    }
}

internal class NotLoadedDictionaryException : Exception()
