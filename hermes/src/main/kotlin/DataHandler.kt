package systemkern

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import systemkern.utils.Utils
import java.io.File
import java.io.InputStream
import java.util.ArrayList

class DataHandler(
    private val gson : Gson = Gson(),
    private var classes : MutableList<String> = mutableListOf(),
    private val documents : MutableList<MutableMap<ArrayList<String>?,String>> = mutableListOf()){
    var utils : Utils? = null
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
        var tokens : ArrayList<String>?
        for(intent in intents){
            for (pattern in intent.patterns){
                tokens = tokenizePhrases(cleanText(pattern))
                if (tokens != null) {
                    for(token in tokens){
                        documents.add(mutableMapOf(Pair(tokens,intent.tag)))
                        if(!classes.contains(intent.tag)){
                            classes.add(intent.tag)
                        }
                    }
                }
            }
        }
        createArrayOfZerosAndOnes()
    }

    private fun createArrayOfZerosAndOnes(){
        for(doc in documents){
            var bag = listOf<Int>()
            val pattern_words = doc[doc.keys.first()]
            print(pattern_words)
        }
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