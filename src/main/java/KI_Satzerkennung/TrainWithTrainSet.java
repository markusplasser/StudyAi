package KI_Satzerkennung;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javafx.css.parser.Token;

import java.io.File;
import java.util.*;

public class TrainWithTrainSet {
    /**
     * Es gibt 2 Netze einmal für kurze und lange Sätze #
     * klein <= 10 rest groß
     */

    public static void main(String[] args){
        TrainWithTrainSet train = new TrainWithTrainSet();
        //train.trainWithdata(false,"res/saveLarge.txt");
        train.runSentenceThrough("Der zwiete Weltkrieg ist 1940 ausgebrochen und hat millionen von Leuten das Leben gekostet.");

    }

    public void trainWithdata(boolean klein, String save){
        try{
            int[] kurz = {22 ,16 ,8 , 2};
            int[] lang = {35,24,12,2};
            Network network = Network.loadNetwork(save);
            TrainSet set = createSet(klein);

            traindata(network,set,1000,350,200,true,save);
            network.saveNetwork(save);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }



    public void runSentenceThrough(String txt)  {
        Tokenizer tk = new Tokenizer();
        try {
            String save = "res/saveSmall.txt";
            int targetlength = 22;
            if(!getCategory(txt)){
                save = "res/saveLarge.txt";
                targetlength = 35;
            }
            tk.loadTokenizer("res/tokenizer.json");
            Network network = Network.loadNetwork(save);

            double[] input = tk.normalize(tk.tokenizeBatch(new String[]{txt}, targetlength)[0]);
            System.out.println(Arrays.toString(input));
            double[] ergebnis =network.checkSentence(input);
            System.out.println(txt);
            System.out.println("Question: " + ergebnis[0]);
            System.out.println("Statement: " + ergebnis[1]);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public double[] padOrTruncate(double[] tokens, int targetLength) {
        double[] result = new double[targetLength];
        int copyLength = Math.min(tokens.length, targetLength);
        System.arraycopy(tokens, 0, result, 0, copyLength);
        return result;
    }


    public TrainSet createSet(boolean klein){
        int inputsize = klein ? 22 : 35;
        TrainSet trainSet = new TrainSet(inputsize,2);
        trainSet = fillTrainSetQA(trainSet,klein);
        return trainSet;
    }



    public void traindata(Network net, TrainSet trainSet,int epoch ,int batchsize, int anz,boolean checkAnswers, String save){
        for(int i = 0; i < epoch; i++){
            net.train(trainSet,batchsize,anz);
            if(i%100 == 0 && checkAnswers){
                System.out.println(i+":Runden");
                try{
                    net.saveNetwork(save);
                }catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
    }



    public TrainSet fillTrainSetQA(TrainSet trainSet, boolean klein){
        try {
            File jsonFile = new File("Translated_German_SQuAD-Train-v1.1.json");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonFile);

            List<String> allQuestions = new ArrayList<>();
            List<String> allAnswers = new ArrayList<>();

            // 2. Durch die SQuAD-Struktur iterieren
            JsonNode dataArray = root.get("data");
            for (JsonNode article : dataArray) {
                for (JsonNode paragraph : article.get("paragraphs")) {

                    // Fragen extrahieren
                    for (JsonNode qa : paragraph.get("qas")) {
                        allQuestions.add(qa.get("question").asText());

                        // Antworten extrahieren (falls vorhanden)
                        if (qa.has("answers") && qa.get("answers").size() > 0) {
                            allAnswers.add(qa.get("answers").get(0).get("text").asText());
                        }
                    }
                }
            }



            Random r =  new Random();
            Tokenizer tk = new Tokenizer();
            tk.loadTokenizer("res/tokenizer.json");

            // Training für den Tokenizer
//            StringBuilder korpus = new StringBuilder();
//            for(int i = 0; i < 5000; i++){
//                int idx = r.nextInt(allQuestions.size());
//                korpus.append(allQuestions.get(idx)).append(" ");
//                korpus.append(allAnswers.get(idx)).append(" ");
//            }
//            tk.train(korpus.toString(), 5000, 2);

//              Durchschnittliche länge der Sätze
//            double questionSum = 0;
//            double answerSum = 0;
//
//            for(int i = 0; i < allQuestions.size(); i++){
//                questionSum += allQuestions.get(i).length();
//                answerSum += allAnswers.get(i).length();
//            }
//            System.out.println("Frage länge: " + questionSum/ allQuestions.size());
//            System.out.println("Antwort länge: " + answerSum / allAnswers.size());
            double[] targetqu = {1,0};
            double[] targetan = {0,1};

            if(klein){
                List<String> filteredQuestions = new ArrayList<>();
                List<String> filteredAnswers = new ArrayList<>();

                for (String q : allQuestions) {
                    if (getCategory(q)) filteredQuestions.add(q);
                }
                for (String a : allAnswers) {
                    if (getCategory(a)) filteredAnswers.add(a);
                }
                String[] qu = filteredQuestions.toArray(new String[0]);
                String[] an = filteredAnswers.toArray(new String[0]);


                double[][] batchqu = tk.tokenizeBatch(qu,22);
                double[][] batchan = tk.tokenizeBatch(an,22);

                int smaller = Math.min(batchan.length, batchqu.length);
                for(int i = 0; i< smaller;i++){
                    trainSet.add(tk.normalize(batchqu[i]),targetqu);
                    trainSet.add(tk.normalize(batchan[i]),targetan);
                }

            }
            else{
                List<String> filteredQuestions = new ArrayList<>();
                List<String> filteredAnswers = new ArrayList<>();

                for (String q : allQuestions) {
                    if (!getCategory(q)) filteredQuestions.add(q);
                }
                for (String a : allAnswers) {
                    if (!getCategory(a)) filteredAnswers.add(a);
                }
                String[] qu = filteredQuestions.toArray(new String[0]);
                String[] an = filteredAnswers.toArray(new String[0]);


                double[][] batchqu = tk.tokenizeBatch(qu,35);
                double[][] batchan = tk.tokenizeBatch(an,35);

                int smaller = Math.min(batchan.length, batchqu.length);
                for(int i = 0; i< smaller;i++){
                    trainSet.add(tk.normalize(batchqu[i]),targetqu);
                    trainSet.add(tk.normalize(batchan[i]),targetan);
                }
            }

            System.out.println("TrainSet Größe: " + trainSet.size());
            return trainSet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean getCategory(String text) {
        int wordCount = text.split("\\s+").length;
        if (wordCount <= 10)       return true;
        else                      return false;
    }
}
