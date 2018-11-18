package systemkern.hermes

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.Collections.sort
import javax.persistence.Entity
import javax.persistence.Id

@Component
internal class NaturalLanguagePreProcessor(
    private val wordRepository: WordRepository,
    private val googleJson : Gson = Gson(),
    internal val classes : MutableList<String> = mutableListOf(),
    private val documents : MutableList<MutableMap<String,ArrayList<String>?>> = mutableListOf(),
    var bags: Array<DoubleArray> = arrayOf(),
    internal var classesIntoArrays: Array<DoubleArray> = arrayOf(),
    private val hermesUtils : HermesUtils = HermesUtils(),
    private var bagLength : Int = 0,
    private var patternWords : ArrayList<String>? = null,
    var intentList : List<Intent>? = null) {

    internal lateinit var words : ArrayList<String>

        fun loadDictionary() : Boolean{
            words = wordRepository.returnArrayOfWords()
            bagLength = words.size
            return words.isEmpty()
        }

        // To load file where structured text is located
        fun loadJsonFile(pathFileToFile: String){
            val inputStream: InputStream = File(pathFileToFile).inputStream()
            val dataJson = inputStream.bufferedReader().use { it.readText() }
            intentList = googleJson.fromJson(dataJson, object : TypeToken<List<Intent>>() {}.type)
        }

        // To create arrays of documents,words and classes, classes are useful to classify words into categories,
        // documents gather tokens(words in sentences) and classes.
        fun tokenizeWordsInIntents(){
            var tokens : ArrayList<String>?
            for(intent in intentList!!){
                for (pattern in intent.patterns){
                    tokens = hermesUtils.tokenizeSentence(pattern)
                    documents.add(mutableMapOf(Pair(intent.tag,tokens!!)))
                    for(token in tokens){
                        if (!(words.contains(token))){
                            words.add(token)
                            persistDictionary(token,words.lastIndex)
                        }
                        if(!classes.contains(intent.tag)){
                            classes.add(intent.tag)
                        }
                    }
                }
            }
            sort(words)
            createArrayOfZerosAndOnes()
        }

        fun persistDictionary(word : String,index : Int){
            wordRepository.save(Word(index,word))
        }

        private fun createArrayOfZerosAndOnes(){
            var bag : DoubleArray = doubleArrayOf()
            for(doc in documents){
                patternWords = doc[doc.keys.first()]
                    for (word in words){
                        if(patternWords?.contains(word) as Boolean){
                            bag = bag.plus(1.0)
                        }else {
                            bag = bag.plus(0.0)
                        }
                    }
                bags = bags.plus(bag)
                val outputRow = createArrayListOfZeros(classes.size)
                outputRow[classes.indexOf(doc.keys.first())] = 1.0
                classesIntoArrays = classesIntoArrays.plus(outputRow)
                bag = doubleArrayOf()
            }
            bagLength = bags[0].size
        }

        //Utility to create array filled out with zeros
        private fun createArrayListOfZeros(arraySize : Int) : DoubleArray{
            var listToFill = doubleArrayOf()
            for (i in 0..(arraySize - 1)){
                listToFill = listToFill.plus(0.0)
            }
            return listToFill
        }

        // Sentence representation of zeros and ones
        fun sentence2array(sentence : String) : DoubleArray {
            val wordsInSentence = hermesUtils.tokenizeSentence(sentence)
            val zerosAndOnesArray = createArrayListOfZeros(bagLength)
            for (word in wordsInSentence!!) {
                if(words.contains(word)){
                    zerosAndOnesArray[words.indexOf(word)] = 1.0
                }
            }
            return zerosAndOnesArray
        }

    }

//Representation of json file structure
data class Intent(
    val tag : String,
    val patterns : List<String>,
    val responses : List<String>,
    val context_set : String)

@Repository
internal interface WordRepository : CrudRepository<Word, Int> {
    @Query(value = "Select value from Word")
    fun returnArrayOfWords() : ArrayList<String>
}

@Entity
internal data class Word(
    @Id
    val id : Int,
    val value : String
)
