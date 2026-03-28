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
        train.runSentenceThrough("Was bezeichnete die Times, nachdem sie 1920 antisemitische Leitartikel veröffentlicht hatte, als Juden?");

    }

    public void trainWithdata(){
        try{
            int[] start = {19,10,2};
            Network network = Network.loadNetwork("res/save19.txt");
            TrainSet set = createSet(60000);

            traindata(network,set,3000,70,500,true);
            network.saveNetwork("res/save19.txt");
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);

        }
    }



    public void runSentenceThrough(String txt)  {
        Tokenizer tk = new Tokenizer();
        try {
            Network network = Network.loadNetwork("res/save19.txt");
            String[] tmp = new String[1];
            tmp[0] = txt;
            double[][] input = tk.tokenizeBatch(tmp,19);
            System.out.println(Arrays.toString(input[0]));
            double[] ergebnis =network.checkSentence(input[0]);
            System.out.println(txt);
            System.out.println("Question: " + ergebnis[0]);
            System.out.println("Statement: " + ergebnis[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    public TrainSet createSet(int size){

        TrainSet trainSet = new TrainSet(19,2);
        trainSet = fillTrainSetQA(trainSet,size);
        return trainSet;
    }



    public void traindata(Network net, TrainSet trainSet,int epoch ,int batchsize, int anz,boolean checkAnswers){
        for(int i = 0; i < epoch; i++){
            net.train(trainSet,batchsize,anz);
            if(i%100 == 0 && checkAnswers){
                System.out.println(i+":Runden");
//                try{
//                    net.saveNetwork("res/save19.txt");
//                }catch(Exception e){
//                    throw new RuntimeException(e);
//                }
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

            for(int i = 0 ;i<allAnswers.size();i++){
                tk.train(allQuestions.get(i),5000,2);
                tk.train(allAnswers.get(i),5000,2);
            }

            for(int i = 0; i < 10; i++){
                System.out.println(allQuestions.get(r.nextInt(allAnswers.size())));
            }

            String[] qu = allQuestions.toArray(new String[0]);
            double[][] batch = tk.tokenizeBatch(qu,19);
            String[] an = allAnswers.toArray(new String[0]);
            double[][] batch2 = tk.tokenizeBatch(an,19);
            double[] target = {1,0};
            double[] target1 = {0,1};
            for(int i = 0; i < size/2; i = i+2)
            {
                trainSet.add(batch[i],target);
                trainSet.add(batch2[i],target1);
            }

            return trainSet;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
