package KI_Satzerkennung;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.util.*;

public class TrainWithTrainSet {
    /**
     *
     * klein <= 6
     * mittelklein > 6 && <= 10
     * mittelgroß > 10 && <= 14
     * groß > 14
     */


    public static void main(String[] args){
        TrainWithTrainSet train = new TrainWithTrainSet();
        //train.trainWithdata(4,"res/save328.txt");
        train.runSentenceThrough("Wenn du morgen tot bist was würdest du heute noch machen?");
    }

    /**
     * richtet alles her damit die train methode aufgerufen werden kann
     *
     * @param size -satzlänge
     * @param save -path zur Savedatei
     */
    public void trainWithdata(int size, String save){
        try{
            int[] netz1 = {136, 64, 32, 2};
            int[] netz2 = {176, 64, 32, 2};
            int[] netz3 = {232, 64, 32, 2};
            int[] netz4 = {328, 64, 32, 2};
            int[] layers = size == 1 ? netz1 : size == 2 ? netz2 : size == 3 ? netz3 : netz4;
            Network network = new Network(5000,8,layers);
            TrainSet set = createSet(size);

            traindata(network,set,500,150,200,true,save);
            network.saveNetwork(save);
        }catch (Exception e){
            System.out.println("Problem with loading the Network\nPath: " + save + " dose not exist!");
        }
    }


    /**
     * Lässt einen Satz testen und gibt das double[] zurück worin sich die Antwort befindet
     * gnazes double arr weil die auswertung noch folgt...
     * @param txt
     */
    public double[] runSentenceThrough(String txt)  {
        Tokenizer tk = new Tokenizer();
        try {
            int length = getCategory(txt);
            int targetlength = 41;
            String save = "res/save328.txt";

            switch (length){
                case 1:
                    targetlength = 17;
                    save = "res/save136.txt";
                    System.out.println("Verwendete Länge: 1-kurz");
                    break;
                case 2:
                    targetlength = 22;
                    save = "res/save176.txt";
                    System.out.println("Verwendete Länge: 2-mittel-kurz");
                    break;
                case 3:
                    targetlength = 29;
                    save = "res/save232.txt";
                    System.out.println("Verwendete Länge: 3-mittel-lang");
            }
            if(targetlength == 41){
                System.out.println("Verwendete Länge: 4-lang");
            }



            tk.loadTokenizer("res/tokenizer.json");
            Network network = Network.loadNetwork(save);

            double[] input = tk.tokenizeBatch(new String[]{txt}, targetlength)[0];
            double[] ergebnis =network.checkSentence(input);
            System.out.println(txt);
            System.out.println("Question: " + ergebnis[0]);
            System.out.println("Statement: " + ergebnis[1]);
            return ergebnis;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //helped with testing
    //fills/shortans an arr to a desired length
    public double[] padOrTruncate(double[] tokens, int targetLength) {
        double[] result = new double[targetLength];
        int copyLength = Math.min(tokens.length, targetLength);
        System.arraycopy(tokens, 0, result, 0, copyLength);
        return result;
    }

    /**
     * erzeugt ein TrainSet und lässt es befüllen
     * liefer das befüllte TrainSet zurück.
     * @param size
     * @return
     */
    public TrainSet createSet(int size){
        int inputsize = size == 1 ? 17 : size == 2 ? 22 : size == 3 ? 29 : 41;
        TrainSet trainSet = new TrainSet(inputsize,2);
        trainSet = fillTrainSetQA(trainSet,size);
        return trainSet;
    }


    /**
     * trainiert das Netz epoch -mal
     * @param net
     * @param trainSet
     * @param epoch
     * @param batchsize
     * @param anz
     * @param checkAnswers
     * @param save
     */
    public void traindata(Network net, TrainSet trainSet, int epoch, int batchsize, int anz, boolean checkAnswers, String save){
        for(int i = 0; i < epoch; i++){
            net.train(trainSet, batchsize, anz);
            if(i % 100 == 0 && checkAnswers){
                System.out.println(i + " Runden - Loss: " + calcLoss(net, trainSet));
            }
        }
    }

    private double calcLoss(Network net, TrainSet trainSet){
        double totalLoss = 0;
        int samples = Math.min(500, trainSet.size());
        for(int i = 0; i < samples; i++){
            double[] output = net.calculate(net.embedInput(trainSet.getInput(i)));
            double[] target = trainSet.getTarget(i);
            for(int j = 0; j < output.length; j++){
                totalLoss += Math.pow(output[j] - target[j], 2);
            }
        }
        return totalLoss / samples;
    }


    /**
     * befüllt und gibt ein TrainSet zurück
     * + entweder lange oder kurze Sätze befüllen
     * @param trainSet - Trainset
     * @param length - länge des Netzbereiches das triniert wird
     * @return {@link TrainSet}
     */
    public TrainSet fillTrainSetQA(TrainSet trainSet, int length){
        try {
            //aus dem Internet für das dataset
            //lässt einen auf alle Sätze des datasets zugreifen
            File jsonFile = new File("Translated_German_SQuAD-Train-v1.1.json");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonFile);

            List<String> allQuestions = new ArrayList<>();
            List<String> allAnswers = new ArrayList<>();

            // 2. Durch die SQuAD-Struktur iterieren
            JsonNode dataArray = root.get("data");
            for (JsonNode article : dataArray) {
                for (JsonNode paragraph : article.get("paragraphs")) {
                    String context = paragraph.get("context").asText();
                    String[] sentences = context.split("(?<=[.!?])\\s+");

                    for (JsonNode qa : paragraph.get("qas")) {
                        // Frage hinzufügen
                        allQuestions.add(qa.get("question").asText());

                        // Vollständigen Satz aus Kontext nehmen, nicht das kurze Answer-Fragment
                        if (qa.has("answers") && !qa.get("answers").isEmpty()) {
                            String answerText = qa.get("answers").get(0).get("text").asText();

                            for (String sentence : sentences) {
                                if (sentence.contains(answerText)
                                        && sentence.endsWith(".")) {            // vollständiger Satz
                                    allAnswers.add(sentence.trim());
                                    break;
                                }
                            }
                        }
                    }
                }
            }



            //selber gemacht ab hier!
            //füllt das Trainset mit gleich vielen Antworten/Fragen
            //++ unterschiedliche Längen werden getrennt
            Tokenizer tk = new Tokenizer();
            tk.loadTokenizer("res/tokenizer.json");


//            ***************** Training für den Tokenizer *****************
//            StringBuilder korpus = new StringBuilder();
//            for(int i = 0; i < 5000; i++){
//                int idx = r.nextInt(allQuestions.size());
//                korpus.append(allQuestions.get(idx)).append(" ");
//                korpus.append(allAnswers.get(idx)).append(" ");
//            }
//            tk.train(korpus.toString(), 5000, 2);

//            *****************Durchschnittliche länge der Sätze ************
//            double questionSum = 0;
//            double answerSum = 0;
//
//            for(int i = 0; i < allQuestions.size(); i++){
//                questionSum += allQuestions.get(i).length();
//                answerSum += allAnswers.get(i).length();
//            }
//            System.out.println("Frage länge: " + questionSum/ allQuestions.size());
//            System.out.println("Antwort länge: " + answerSum / allAnswers.size());

            //fixed target values
            double[] targetqu = {1,0};
            double[] targetan = {0,1};

            ArrayList<String> arrqu = new ArrayList<>();
            ArrayList<String> arran = new ArrayList<>();
            int maxlength =0;
            int smaller = Math.min(allAnswers.size(),allQuestions.size());
            switch (length){
                case 1:
                    for(int i = 0; i < smaller; i++){
                        if(getCategory(allQuestions.get(i)) == 1){
                            arrqu.add(allQuestions.get(i));
                        }
                        if(getCategory(allAnswers.get(i)) == 1){
                            arran.add(allAnswers.get(i));
                        }
                    }
                    maxlength = 17;
                    break;
                case 2:
                    for(int i = 0; i < smaller; i++){
                        if(getCategory(allQuestions.get(i)) == 2){
                            arrqu.add(allQuestions.get(i));
                        }
                        if(getCategory(allAnswers.get(i)) == 2){
                            arran.add(allAnswers.get(i));
                        }
                    }
                    maxlength = 22;
                    break;
                case 3:
                    for(int i = 0; i < smaller; i++){
                        if(getCategory(allQuestions.get(i)) == 3){
                            arrqu.add(allQuestions.get(i));
                        }
                        if(getCategory(allAnswers.get(i)) == 3){
                            arran.add(allAnswers.get(i));
                        }
                    }
                    maxlength = 29;
                    break;
                case 4:
                    for(int i = 0; i < smaller; i++){
                        if(getCategory(allQuestions.get(i)) == 4){
                            arrqu.add(allQuestions.get(i));
                        }
                        if(getCategory(allAnswers.get(i)) == 4){
                            arran.add(allAnswers.get(i));
                        }
                    }
                    maxlength = 41;
                    break;
                default:
                    maxlength = 0;
            }

            smaller = Math.min(arrqu.size(),arran.size());
            for(int i = 0; i < smaller; i++){
                trainSet.add(tk.tokenizeBatch(new String[]{arrqu.get(i)},maxlength)[0] ,targetqu);
                trainSet.add(tk.tokenizeBatch(new String[]{arran.get(i)},maxlength)[0] ,targetan);
            }
            System.out.println("Fragen Kategorie 2:    " + arrqu.size());
            System.out.println("Antworten Kategorie 2: " + arran.size());

            for(int i = 0; i < 10; i++){
                System.out.println("Statement: " + arran.get(i));
                System.out.println("Frage: " + arrqu.get(i));
            }

            System.out.println("TrainSet Größe: " + trainSet.size());
            System.out.println("Fragen gesamt: " + allQuestions.size());
            System.out.println("Antworten gesamt: " + allAnswers.size());
            return trainSet;
        } catch (Exception e) {
            System.out.println("Probleme mit dem Befüllen des TrainSets...");
            return null;
        }
    }

    /**
     * 0 -> <=5
     * 1 -> >5 & <=10
     * 2 -> >10 & <= 15
     * 3 -> >15
     * @param text
     * @return
     */
    private int getCategory(String text) {
        int wordCount = text.split("\\s+").length;
        if(wordCount <= 5) return 1;
        if(wordCount <= 10) return 2;
        if(wordCount <= 15) return 3;
        return 4;
    }
}
