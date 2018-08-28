package systemkern.utils;
import smile.nlp.stemmer.LancasterStemmer;
import java.util.ArrayList;

public class Utils {
    private LancasterStemmer stemmer = new LancasterStemmer();

    public ArrayList<String> tokenize(String phrase){
        ArrayList<String> result = new ArrayList<>();

        String[] aux = phrase.split(" ");
        for(int i = 0 ; i < aux.length ; i++){
            result.add(stemmer.stem(aux[i]));
        }
        return result;
    }

 /*   public ArrayList<String> sortArrayOfWords(ArrayList<String> insumo){

    }*/
}
