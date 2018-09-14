package systemkern.hermes

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.InputStream
import java.io.FileNotFoundException

@Configuration
@ConfigurationProperties("systemkern.hermes")
internal class HermesConfiguration(
    private val numberOfEpochsRequiredForTraining : Int = 100,
    private var pathToPackage : String = "",
    var pathToTrainedModel : Array<String> = arrayOf("src","main","resources"),
    var pathToJson : Array<String> = pathToTrainedModel.plus("training_doc.txt"),
    var pathToJsonString : String? = null,
    var pathToTrainedModelString : String? = null,
    var nnImpl : NeuralNetworkImplementation? = null,
    var inputStream : InputStream? = null,
    val fileName : String = "trained_model.zip",
    val pathSeparator : String = System.getProperty("file.separator")) {

    @Autowired
    internal var nlpProcessor : NaturalLanguagePreProcessor? = null

    @Bean
    fun loadAndTrainModel() : NeuralNetworkImplementation{
        initializeConstants()
        nnImpl = NeuralNetworkImplementation(numberOfEpochsRequiredForTraining)
        nlpProcessor!!.loadJsonFile(pathToJsonString as String)
        nnImpl!!.nlpProcessor = nlpProcessor
        try {
            if(!nlpProcessor!!.loadDictionary()){
                inputStream = File(pathToTrainedModelString + pathSeparator + fileName).inputStream()
                nnImpl!!.loadModel(pathToTrainedModelString + pathSeparator + fileName)
            }else
            {
                throw NotLoadedDictionaryException()
            }
        }catch (e : Exception){
            nlpProcessor!!.tokenizeWordsInIntents()
            nnImpl!!.configureModel()
            nnImpl!!.trainMNN()
            nnImpl!!.saveTrainedModel(pathToTrainedModelString + pathSeparator + fileName)
        }
        return nnImpl as NeuralNetworkImplementation
    }

    fun initializeConstants(){
        pathToPackage  =
            HermesConfiguration::class
            .java
            .protectionDomain
            .codeSource
            .location
            .path
            .split("target")[0]
        pathToPackage = pathToPackage.subSequence(1,pathToPackage.length.minus(1)) as String
        pathToPackage = pathToPackage.replace("/", pathSeparator)
        pathToPackage = pathToPackage.replace("\"", pathSeparator)
        pathToJson[0] = pathToPackage + pathSeparator + pathToJson[0]
        pathToTrainedModel[0] = pathToPackage + pathSeparator + pathToTrainedModel[0]
        pathToJsonString = joinPathParts(pathToJson)
        pathToTrainedModelString = joinPathParts(pathToTrainedModel)
    }

    private fun joinPathParts(arrayOfStringParts : Array<String>) : String {
        var fullPath = ""
        for(part in arrayOfStringParts){
            fullPath = fullPath + pathSeparator + part
        }
        return fullPath.substring(1,fullPath.length)
    }
}

internal class NotLoadedDictionaryException : Exception()
