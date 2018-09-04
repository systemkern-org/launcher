package systemkern

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import smile.regression.NeuralNetwork
import systemkern.hermes.NeuralNetworkImplementation
import systemkern.utils.Utils
import java.io.File
import java.io.InputStream
import java.util.ArrayList
import java.util.Collections.sort

class DataHandler(
    private val googleJson : Gson = Gson(),
    private var classes : MutableList<String> = mutableListOf(),
    private val documents : MutableList<MutableMap<ArrayList<String>?,String>> = mutableListOf(),
    private var bags: Array<DoubleArray> = arrayOf(),
    private var outPutRows: Array<DoubleArray> = arrayOf(),
    private var words : ArrayList<String> = arrayListOf(),
    private var utils : Utils = Utils()) {


    //TODO: the next element will be moved when tests are finished
    private var nnImpl : NeuralNetworkImplementation? = null
    //TODO:-------------------------------------------------------

    var bagLength = 0

    // To load file where structured text is located
        fun loadJsonFile(pathFileToFile: String) : List<Intent>{
            val inputStream: InputStream = File(pathFileToFile).inputStream()
            val dataJson = inputStream.bufferedReader().use { it.readText() }
            return googleJson.fromJson(dataJson, object : TypeToken<List<Intent>>() {}.type)
        }

        //To create arrays of words from phrases,spaces and tokens of language are deleted
        private fun tokenizePhrases(phrase : String) : ArrayList<String>? {
           return utils.tokenize(cleanText(phrase))
        }

        // To create arrays of documents,words and classes, classes are useful to classify words into categories,
        // documents gather tokens(words in sentences) and classes.
        fun tokenizeWordsInIntents(intents : List<Intent>){
            var tokens : ArrayList<String>?
            for(intent in intents){
                for (pattern in intent.patterns){
                    tokens = tokenizePhrases(pattern)
                    if (tokens != null) {
                        documents.add(mutableMapOf(Pair(tokens,intent.tag)))
                        for(token in tokens){
                            if (!(words.contains(token))){
                                words.add(token)
                            }
                            if(!classes.contains(intent.tag)){
                                classes.add(intent.tag)
                            }
                        }
                    }
                }
            }
            sort(words)
            createArrayOfZerosAndOnes()
        }


        private fun createArrayOfZerosAndOnes(){
            var bag : DoubleArray = doubleArrayOf()
            for(doc in documents){
                val patterWords = doc[doc.keys.first()]
                    for (word in words){
                        if(patterWords?.contains(word) as Boolean){
                            bag = bag.plus(1.0)
                        }else {
                            bag = bag.plus(0.0)
                        }
                    }
                bags = bags.plus(bag)
                val outputRow = createArrayListOfZeros(classes.size)
                outputRow[classes.indexOf(doc[doc.keys.first()])] = 1.0
                outPutRows = outPutRows.plus(outputRow)
                bag = doubleArrayOf()
            }
            bagLength = bags[0].size
            nnImpl = NeuralNetworkImplementation(NeuralNetwork.ActivationFunction.LOGISTIC_SIGMOID,words.size)
            trainNeuralNetwork(bags,outPutRows)
            val res = testsSentences()
            print(res)
        }

        //Utility to create arrays filled out with zeros
        private fun createArrayListOfZeros(arraySize : Int) : DoubleArray{
            var listToFill = doubleArrayOf()
            for (i in 0..(arraySize - 1)){
                listToFill = listToFill.plus(0.0)
            }
            return listToFill
        }

        private fun trainNeuralNetwork(train_x : Array<DoubleArray>,train_y : Array<DoubleArray>){
            for (y in train_y) {
                nnImpl?.learn(arrayOf(train_x[train_y.indexOf(y)]),train_y[train_y.indexOf(y)])
            }
        }

        private fun checkSentenceInDictionary(sentence : String) : DoubleArray {
            val wordsInSentence = tokenizePhrases(sentence)
            var zerosAndOnesArray = createArrayListOfZeros(bagLength)

            if(wordsInSentence  !== null) {
                for (word in wordsInSentence) {
                    if(words.contains(word)){
                        zerosAndOnesArray[words.indexOf(word)] = 1.0
                    }
                }
            }

            return zerosAndOnesArray
        }

        private fun testsSentences() : DoubleArray? {
            val testSentence = "Hello how are you?"
            return nnImpl?.predict(arrayOf(checkSentenceInDictionary(testSentence)))
        }

        // To delete contractions and useless tokens from phrases
        private fun cleanText(text: String) : String {
            var finalText = text
            finalText = finalText.toLowerCase()
            finalText = finalText.replace("i'm", "i am")
            finalText = finalText.replace("\'s", " is")
            finalText = finalText.replace("\'ll", " will")
            finalText = finalText.replace("\'ve", " have")
            finalText = finalText.replace("\'re", " are")
            finalText = finalText.replace("\'d", " would")
            finalText = finalText.replace("won't", "will not")
            finalText = finalText.replace("can't", "cannot")
            finalText = finalText.replace("don't", " do not")
            finalText = finalText.replace("doesn't", " does not")
            return finalText.replace("[-()\"#/@;:<>{}+=~|.?,]", "")
        }
    }

//Representation of json file structure
data class Intent(
    val tag : String,
    val patterns : List<String>,
    val responses : List<String>,
    val context_set : String)
