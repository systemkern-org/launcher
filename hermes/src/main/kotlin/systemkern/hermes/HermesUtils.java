package systemkern.hermes;

import smile.nlp.stemmer.LancasterStemmer;
import java.util.ArrayList;

public class HermesUtils {
    private LancasterStemmer stemmer = new LancasterStemmer();

    private ArrayList<String> tokenize(String phrase){
        ArrayList<String> result = new ArrayList<>();

        String[] aux = phrase.split(" ");
        for(int i = 0 ; i < aux.length ; i++){
            result.add(stemmer.stem(aux[i]));
        }
        return result;
    }

    //To create arrays of words from phrases,spaces and tokens of language are deleted
    public ArrayList<String> tokenizeSentence(String sentence) {
        return tokenize(cleanText(sentence));
    }

    // To delete contractions and useless tokens from phrases
    private String cleanText(String text) {
        String finalText = text;
        finalText = finalText.toLowerCase();
        finalText = finalText.replace("i'm", "i am");
        finalText = finalText.replace("\'s", " is");
        finalText = finalText.replace("\'ll", " will");
        finalText = finalText.replace("\'ve", " have");
        finalText = finalText.replace("\'re", " are");
        finalText = finalText.replace("\'d", " would");
        finalText = finalText.replace("won't", "will not");
        finalText = finalText.replace("can't", "cannot");
        finalText = finalText.replace("don't", " do not");
        finalText = finalText.replace("doesn't", " does not");
        finalText = finalText.replace("?", "");
        return finalText.replace("[-()\"#/@;:<>{}+=~|.?,]", "");
    }

}
