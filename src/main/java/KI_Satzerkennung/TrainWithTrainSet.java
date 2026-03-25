package KI_Satzerkennung;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrainWithTrainSet {
    public TrainSet createSet(int size){
        int setsize = size;
        TrainSet trainSet = new TrainSet(20,2);
        fillTrainSetQA(trainSet,setsize);
        return trainSet;
    }

    public void fillTrainSetQA(TrainSet trainSet, int size){
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

            System.out.println("Geladene Fragen: " + allQuestions.size());
            System.out.println("Geladene Antworten: " + allAnswers.size());

            Random r =  new Random();
            Tokenizer tk = new Tokenizer(20);
            for(int i = 0; i < size/2; i = i+2)
            {
                double[] input = tk.encode(allQuestions.get(r.nextInt(allQuestions.size())));
                double[] target = {1,0};
                double[] input1 = tk.encode(allAnswers.get(r.nextInt(allAnswers.size())));
                double[] target1 = {0,1};

                trainSet.add(input,target);
                trainSet.add(input1,target1);
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
