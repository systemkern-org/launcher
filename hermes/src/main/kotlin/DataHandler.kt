package systemkern

import java.io.BufferedReader
import java.io.File

class DataHandler(private var bufferedReader: BufferedReader){
    var pathToFile: String = ""

    fun loadLines() : MutableMap<String,String>{
        var line: List<String>
        this.bufferedReader = File(pathToFile).bufferedReader()
        val idToLine = mutableMapOf<String,String>()
        this.bufferedReader.useLines {
            lines -> lines.forEach{
            line = it.split(" +++$+++ ")
                if(line.size == 5){
                    idToLine[line[0]] = line[4]
                }
            }
        }
        return idToLine
    }

    fun loadConversations()/*: MutableList<String>*/{
        this.bufferedReader = File(pathToFile).bufferedReader()
        val conversation_ids: MutableList<String>

        this.bufferedReader.useLines {
            lines -> lines.forEach{

            }
        }

    }
}
