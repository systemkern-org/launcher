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
    private var pathToPackage: String = "",
    private var pathToTrainedModel: Array<String> = arrayOf("src", "main", "resources"),
    private var pathToJson: Array<String> = pathToTrainedModel.plus("training_doc.txt"),
    private var pathToJsonString: String? = null,
    private var pathToTrainedModelString: String? = null,
    private var nnImpl: NeuralNetworkImplementation? = null,
    private var inputStream: InputStream? = null,
    private val fileName: String = "trained_model.zip",
    private val pathSeparator: String = System.getProperty("file.separator")) {

    @Autowired
    internal var nlpProcessor: NaturalLanguagePreProcessor? = null

    @Bean
    fun loadAndTrainModel(): NeuralNetworkImplementation {
        initializeConstants()
        nnImpl = NeuralNetworkImplementation(numberOfEpochsRequiredForTraining)
        nlpProcessor!!.loadJsonFile(pathToJsonString as String)
        nnImpl!!.nlpProcessor = nlpProcessor

        try {
            if (!nlpProcessor!!.loadDictionary()) {
                inputStream = File(pathToTrainedModelString + pathSeparator + fileName).inputStream()
                nnImpl!!.loadModel(pathToTrainedModelString + pathSeparator + fileName)
            } else {
                throw NotLoadedDictionaryException()
            }
        } catch (e: Exception) {
            nlpProcessor!!.tokenizeWordsInIntents()
            nnImpl!!.configureModel()
            nnImpl!!.trainMNN()
            //nnImpl!!.saveTrainedModel(pathToTrainedModelString + pathSeparator + fileName)
        }
        return nnImpl as NeuralNetworkImplementation
    }

    fun initializeConstants() {
        pathToPackage =
            HermesConfiguration::class
                .java
                .protectionDomain
                .codeSource
                .location
                .path
                .split("target")[0]
        pathToPackage = pathToPackage.subSequence(1, pathToPackage.length.minus(1)) as String
        pathToPackage = pathToPackage.replace("/", pathSeparator)
        pathToPackage = pathToPackage.replace("\"", pathSeparator)
        pathToJson[0] = pathToPackage + pathSeparator + pathToJson[0]
        pathToTrainedModel[0] = pathToPackage + pathSeparator + pathToTrainedModel[0]
        pathToJsonString = joinPathParts(pathToJson)
        pathToTrainedModelString = joinPathParts(pathToTrainedModel)
    }

    private fun joinPathParts(arrayOfStringParts: Array<String>): String {
        var fullPath = ""
        for (part in arrayOfStringParts) {
            fullPath = fullPath + pathSeparator + part
        }
        return fullPath.substring(1, fullPath.length)
    }
}

internal class NotLoadedDictionaryException : Exception()
