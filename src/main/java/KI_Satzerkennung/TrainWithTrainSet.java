package KI_Satzerkennung;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javafx.css.parser.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TrainWithTrainSet {
    public static void main(String[] args){
        TrainWithTrainSet train = new TrainWithTrainSet();
        //train.trainWithdata();
        train.runSentenceThrough("Abraham Linkon war der erste Präsident der Vereinigten Staaten von Amerika.");

    }

    public void trainWithdata(){
        try{
            int[] start = {32, 64, 32, 2};
            Network network = Network.loadNetwork("res/save32.txt");
            TrainSet set = createSet(150000);

            traindata(network,set,5000,300,100,true);
            network.saveNetwork("res/save32.txt");
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }



    public void runSentenceThrough(String txt)  {
        Tokenizer tk = new Tokenizer();
        try {
            tk.loadTokenizer("res/tokenizer.json");
            Network network = Network.loadNetwork("res/save32.txt");

            double[] input = padOrTruncate(tk.tokenizeNormalized(txt),32);
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
        // Rest bleibt automatisch 0 (Java initialisiert Arrays mit 0)
        return result;
    }


    public TrainSet createSet(int size){

        TrainSet trainSet = new TrainSet(32,2);
        trainSet = fillTrainSetQA(trainSet,size);
        return trainSet;
    }



    public void traindata(Network net, TrainSet trainSet,int epoch ,int batchsize, int anz,boolean checkAnswers){
        for(int i = 0; i < epoch; i++){
            net.train(trainSet,batchsize,anz);
            if(i%100 == 0 && checkAnswers){
                System.out.println(i+":Runden");
                try{
                    net.saveNetwork("res/save32.txt");
                }catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
    }



    public TrainSet fillTrainSetQA(TrainSet trainSet, int size){
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

//            StringBuilder korpus = new StringBuilder();
//            for(int i = 0; i < 5000; i++){
//                int idx = r.nextInt(allQuestions.size());
//                korpus.append(allQuestions.get(idx)).append(" ");
//                korpus.append(allAnswers.get(idx)).append(" ");
//            }
//            tk.train(korpus.toString(), 5000, 2);

            tk.loadTokenizer("res/tokenizer.json");
            for(int i = 0; i < 10; i++){
                System.out.println(allQuestions.get(r.nextInt(allAnswers.size())));
            }

            String[] qu = allQuestions.toArray(new String[0]);
            double[][] batch = tk.tokenizeBatch(qu,32);
            String[] an = allAnswers.toArray(new String[0]);
            double[][] batch2 = tk.tokenizeBatch(an,32);
            for(int i = 0; i< batch.length;i++){
                batch[i] = tk.normalize(batch[i]);
                batch2[i] = tk.normalize(batch2[i]);
            }

            double[] target = {1,0};
            double[] target1 = {0,1};
            for(int i = 0; i < batch.length; i++)
            {
                trainSet.add(batch[i],target);
                trainSet.add(batch2[i],target1);
            }

            System.out.println("TrainSet Größe: " + trainSet.size());

            return trainSet;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
