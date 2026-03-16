
package KI_Satzerkennung;

public class Tokenerkennung {
    final private static String[] listMarker = {"a)","b)","c)","1)","2)","3)","1.","2.","3."};
    final private static String[] keyWords = {"Antwort","Lösung","Hinweis",};
    final private static String[] deafaltWords = {"Hallo","Gerne","Natürlich"};
    final private static String[] questionWords = {"Wer", "Wie", "Was", "Warum", "Welche","Wieso","Wesshalb","Wann"};

    public static double checkQuestionmark(String txt){
        if(txt.contains("?")){
            return 1;
        }
        else{
            return 0;
        }
    }

    public static double checkListMarker(String txt){
        int count = 0;
        for(int i = 0; i < listMarker.length;i++){
            if(txt.contains(listMarker[i])){
                return 1;
            }
        }
        return 0;
    }

    public static double checkKeyWords(String txt){
        for(int i = 0; i < keyWords.length;i++){
            if(txt.contains(keyWords[i])){
                return 1;
            }
        }
        return 0;
    }

    public static double checkDeafaltWords(String txt){
        for(int i = 0; i < deafaltWords.length;i++){
            if(txt.contains(deafaltWords[i])){
                return 1;
            }
        }
        return 0;
    }

    public static double checkQuestionWords(String txt){
        for(int i = 0; i < questionWords.length;i++){
            if(txt.contains(questionWords[i])){
                return 1;
            }
        }
        return 0;
    }

    public static double[] order(String txt){
        double[] ret = new double[5];
        ret[0] = checkQuestionmark(txt);
        ret[1] = checkListMarker(txt);
        ret[2] = checkKeyWords(txt);
        ret[3] = checkDeafaltWords(txt);
        ret[4] = checkQuestionWords(txt);
        return ret;
    }

    /**
     * 0 = Question
     * 1 = Possible solution
     * 2 = right answer
     * 3 = somthing else / filler sentece
     * -1 = err
     * @param txt
     * @return
     */
    public static double justCheckNoNetwork(String txt){
        if(checkQuestionmark(txt) == 1 || checkQuestionWords(txt) == 1){
            return 0;
        }else if(checkListMarker(txt) == 1){
            return 1;
        }else if(checkKeyWords(txt) == 1){
            return 2;
        }else if(checkDeafaltWords(txt) == 1){
            return 3;
        }
        return -1;
    }


}
