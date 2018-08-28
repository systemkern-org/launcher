package systemkern

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import systemkern.utils.Utils
import java.io.File
import java.io.InputStream
import java.util.ArrayList
import java.util.Collections.sort

class DataHandler(
    private val gson : Gson = Gson(),
    private var classes : MutableList<String> = mutableListOf(),
    private val documents : MutableList<MutableMap<ArrayList<String>?,String>> = mutableListOf()){

    private var words : MutableList<String>? = null
    private var utils : Utils? = null

    init{
        utils = Utils()
    }

    fun loadJsonFile(pathFileToFile: String) : List<Intent>{
        val inputStream: InputStream = File(pathFileToFile).inputStream()
        val dataJson = inputStream.bufferedReader().use { it.readText() }
        return gson.fromJson(dataJson, object : TypeToken<List<Intent>>() {}.type)
    }

    private fun tokenizePhrases(phrase : String) : ArrayList<String>? {
       return utils?.tokenize(phrase)
    }

    fun tokenizeWordsInIntents(intents : List<Intent>){
        words = mutableListOf()
        var tokens : ArrayList<String>?
        for(intent in intents){
            for (pattern in intent.patterns){
                tokens = tokenizePhrases(cleanText(pattern))
                if (tokens != null) {
                    documents.add(mutableMapOf(Pair(tokens,intent.tag)))
                    for(token in tokens){
                        if (!(words?.contains(token) as Boolean)){
                            words?.add(token)
                        }
                        if(!classes.contains(intent.tag)){
                            classes.add(intent.tag)
                        }
                    }
                }
            }
        }
        sort(words)
        createArrayOfZerosAndOnes(words)
    }

    private fun createArrayOfZerosAndOnes(words : MutableList<String>?){

        for(doc in documents){
            var bag = arrayListOf<Int>()
            val patterWords = doc[doc.keys.first() ]
            if (words != null){
                for (word in words){
                    if(patterWords?.contains(word) as Boolean){
                        bag.add(1)
                    }else {
                        bag.add(0)
                    }
                }
            }
            val outputRow = createArrayListOfZeros()
            outputRow[classes.indexOf(doc[doc.keys.first()])] = 1

        }
    }

    private fun createArrayListOfZeros() : MutableList<Int>{
        val listToFill = mutableListOf<Int>()
        for (i in 0..(classes.size - 1)){
            listToFill.add(0)
        }
        return listToFill
    }

    private fun createAndTrainNeuralNetwork(){
        //In this method i receive the arrays
        //generated from files, then create and }
        // train neural network.
    }

}

private fun cleanText(text: String) : String {
    var text = text
    text = text.toLowerCase()
    text = text.replace("i'm", "i am")
    text = text.replace("\'s", " is")
    text = text.replace("\'ll", " will")
    text = text.replace("\'ve", " have")
    text = text.replace("\'re", " are")
    text = text.replace("\'d", " would")
    text = text.replace("won't", "will not")
    text = text.replace("can't", "cannot")
    text = text.replace("don't", " do not")
    text = text.replace("doesn't", " does not")
    return text.replace("[-()\"#/@;:<>{}+=~|.?,]", "")
}

data class Intent(
    val tag : String,
    val patterns : List<String>,
    val responses : List<String>,
    val context_set : String)