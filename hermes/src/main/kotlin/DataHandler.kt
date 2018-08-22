package systemkern

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStream

class DataHandler(private val gson : Gson = Gson()){

    fun loadJsonFile(pathFileToFile: String) : List<Intent>{
        val inputStream: InputStream = File(pathFileToFile).inputStream()
        val dataJson = inputStream.bufferedReader().use { it.readText() }
        return gson.fromJson(dataJson, object : TypeToken<List<Intent>>() {}.type)
    }

    fun tokenizeWordsInIntents(intents : List<Intent>){
        var tokens : List<String>
        var classes : MutableList<String> = mutableListOf()
        val documents : MutableList<MutableMap<String,String>> = mutableListOf()
        for(intent in intents){
            for (pattern in intent.patterns){
                tokens = pattern.split(" ")
                for(token in tokens){
                    documents.add(mutableMapOf(Pair(cleanText(token),intent.tag)))
                    if(!classes.contains(intent.tag)){
                        classes.add(intent.tag)
                    }
                }
            }
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