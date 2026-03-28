package KI_Satzerkennung;
import java.util.Random;
import parser.*;

import java.util.Arrays;




public class Network {
    //indexing weight : weigths[layer][neuron][prev neuron]
    private double[][][] weights;
    private double[][] bias;
    private double[][] output;
    private double[][] output_derivative;
    private double[][] err_signal;

    private static final double eta = 0.3;
    private int NETWORK_SIZE;
    private int[] NETWORK_LAYER_SIZE;
    private int INPUT_LAYER_SIZE;
    private int OUTPUT_LAYER_SIZE;

    public Network(int... NETWORK_LAYER_SIZE) {
        NETWORK_SIZE = NETWORK_LAYER_SIZE.length;
        this.NETWORK_LAYER_SIZE = NETWORK_LAYER_SIZE;
        INPUT_LAYER_SIZE = NETWORK_LAYER_SIZE[0];
        OUTPUT_LAYER_SIZE = NETWORK_LAYER_SIZE[NETWORK_SIZE-1];

        weights = new double[NETWORK_SIZE][][];
        bias = new double[NETWORK_SIZE][];
        output = new double[NETWORK_SIZE][];
        err_signal = new double[NETWORK_SIZE][];
        output_derivative = new double[NETWORK_SIZE][];

        for(int i = 0; i < NETWORK_SIZE; i++) {
            bias[i] = createRandomArr(NETWORK_LAYER_SIZE[i],-2.5,2.7);
            output[i] = new double[NETWORK_LAYER_SIZE[i]];
            err_signal[i] = new double[NETWORK_LAYER_SIZE[i]];
            output_derivative[i] = new double[NETWORK_LAYER_SIZE[i]];
            if(i > 0){
                weights[i] = createRandomArr(NETWORK_LAYER_SIZE[i],NETWORK_LAYER_SIZE[i-1],-0.5,0.5);
            }
        }
    }



    /**
     * Feedforward process...
     * SUM = bias + (weight[layer][neuron][prevNeuron] * output[layer][prevNeuron] + weight[layer][neuron][prevNeuron+1])
     * @param input
     * @return double[]
     */
    public double[] calculate(double... input){
        if(input.length != INPUT_LAYER_SIZE){
            return null;
        }
        output[0] = input;
        for(int layer = 1; layer < NETWORK_SIZE; layer++){
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZE[layer]; neuron++){
                double sum = bias[layer][neuron];
                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZE[layer-1]; prevNeuron++){
                    sum += weights[layer][neuron][prevNeuron] * output[layer-1][prevNeuron];
                }
                output[layer][neuron] = sigmoid(sum);
                output_derivative[layer][neuron] = output[layer][neuron] * (1-output[layer][neuron]);
            }
        }
        return output[NETWORK_SIZE-1];
    }


     /**
     *Calc. the Cost func
     * Alles wurde selber geschrieben und auch verstanden.
     * Natürlich nicht selber erfunden!!
     * Dieses Video erklärt alle schritte Mathematisch die umsetzung in Java ist selber gemacht!!
     * https://www.youtube.com/watch?v=tIeHLnjs5U8
     * @param traget
     * @return err_signal
     */
    public void backpropagation(double[] traget){
        /**
         * err_signal = (outputVal - target) * output_derivetive[][]
         *
         * err_signal : wie viel Schuld hast du an dem Falschen output
         * output_derivetive -> wie stark ändert sich das Ergebnis bei kleinen Änderungen
         * je kleiner desto weniger hat es einfluss auf die änderung
         */
        for(int neuron = 0; neuron < OUTPUT_LAYER_SIZE; neuron++)
        {
            err_signal[NETWORK_SIZE-1][neuron] = (output[NETWORK_SIZE-1][neuron] - traget[neuron])
                    * output_derivative[NETWORK_SIZE-1][neuron];
        }

        /**
         *  layer = NETWORK_SIZE-2 -> weil es mit dem Inedexing auf die weights sonst Probleme gibt
         *  weights[layer][neuron][prev neuron]
         *
         */
        for(int layer = NETWORK_SIZE-2; layer > 0; layer--){
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZE[layer]; neuron++){
                double sum = 0;
                for(int nextneuron = 0; nextneuron < NETWORK_LAYER_SIZE[layer+1]; nextneuron++){
                    // Die Schuld ist die Summe aller weights die von dem Neuron ausgehen * ihre Wichtigkeit
                    sum += weights[layer+1][nextneuron][neuron] * err_signal[layer+1][nextneuron];
                }
                // Die Schuld ist die Summe aller weights die von dem Neuron ausgehen * ihre Wichtigkeit
                //Die Schuld von dem Neuron = summe der Ganzen schuld * die wichtigkeit die es hatte
                this.err_signal[layer][neuron] = sum* output_derivative[layer][neuron];
            }
        }
    }

    public void train(TrainSet trainSet, int batchsize, int anz){
        for(int i = 0; i < anz; i++){
            TrainSet batch = trainSet.extractBatch(batchsize);
            for(int j = 0; j < batchsize; j++){
                train(batch.getInput(j),batch.getTarget(j));
                //System.out.println(Arrays.toString(batch.getInput(j)) + "\n" + Arrays.toString(batch.getTarget(j)));
            }
        }
    }

    private void train(double[] input, double[] target){
        if(input.length != INPUT_LAYER_SIZE || target.length != OUTPUT_LAYER_SIZE){return;}
        calculate(input);
        backpropagation(target);
        update(eta);
    }

    public double[] checkSentence(double[] input){
        return calculate(input);
    }

    public void update(double eta){
        for(int layer = 1; layer < NETWORK_SIZE; layer++){
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZE[layer]; neuron++){
                for(int prevneuron = 0;  prevneuron < NETWORK_LAYER_SIZE[layer-1]; prevneuron++){
                    /**
                     * -eta -> wollen den Fehler minimieren -> zum minimum der funk.
                     * output[layer-1][prevneuron] -> falls der output 0 war hat der weight keine Schuld am Fehler
                     * err_signal[layer][neuron] -> der Fehler dieses Neurons -> großer/kleiner Fehler viel/wenig anpassen
                     */
                    double delta = - eta * output[layer-1][prevneuron]* err_signal[layer][neuron];
                    weights[layer][neuron][prevneuron] += delta;
                }
                /**
                 * Das selbe wie bei den weights nur dass der bias addiert wird und desshalb immer schuld am Fehler hat
                 */
                double delta = -eta * err_signal[layer][neuron];
                bias[layer][neuron] += delta;
            }
        }
    }

    /**
     * Calculates the sigmoid function with a given value
     * @param val
     * @return
     */
    private double sigmoid(double val){
        return 1 / (1 + Math.exp(-val));
    }


    /**
     * Creates a new double[size][prevsize] and set values between lower_bound and upper_bound
     * @param size
     * @param prevSize
     * @param lower_bound
     * @param upper_bound
     * @return double[][]
     */
    public double[][] createRandomArr(int size,int prevSize,double lower_bound,double upper_bound){
        if(size < 1 || prevSize < 1){
            return null;
        }
        Random r = new Random();
        double[][] arr = new double[size][prevSize];
        for(int i = 0; i < size; i++){
            arr[i] = createRandomArr(prevSize,lower_bound,upper_bound);
        }
        return arr;
    }



    /**
     * Creates a new double arr with val between the parms
     * @param size
     * @param lower_bound
     * @param upper_bound
     * @return double[]
     */
    public double[] createRandomArr(int size,double lower_bound,double upper_bound){
        Random r = new Random();
        double[] arr = new double[size];
        for(int i = 0; i < size; i++){
            arr[i] = r.nextDouble(lower_bound,upper_bound);
        }
        return arr;
    }

    //save Methoden nicht selber geschrieben
    public void saveNetwork(String path)throws Exception{
        Parser p = new Parser();
        p.create(path);
        Node root = p.getContent();
        Node netw = new Node("Network");
        Node ly = new Node("Layers");
        netw.addAttribute(new Attribute("sizes", Arrays.toString(this.NETWORK_LAYER_SIZE)));
        netw.addChild(ly);
        root.addChild(netw);
        for (int layer = 1; layer < this.NETWORK_SIZE; layer++) {

            Node c = new Node("" + layer);
            ly.addChild(c);
            Node w = new Node("weights");
            Node b = new Node("biases");
            c.addChild(w);
            c.addChild(b);

            b.addAttribute("values", Arrays.toString(this.bias[layer]));

            for (int we = 0; we < this.weights[layer].length; we++) {

                w.addAttribute("" + we, Arrays.toString(weights[layer][we]));
            }
        }
        p.close();

    }
    public static Network loadNetwork(String path)throws Exception{
        Parser p = new Parser();

        p.load(path);
        String sizes = p.getValue(new String[] { "Network" }, "sizes");
        int[] si = ParserTools.parseIntArray(sizes);
        Network ne = new Network(si);

        for (int i = 1; i < ne.NETWORK_SIZE; i++) {
            String biases = p.getValue(new String[] { "Network", "Layers", new String(i + ""), "biases" }, "values");
            double[] bias = ParserTools.parseDoubleArray(biases);
            ne.bias[i] = bias;

            for(int n = 0; n < ne.NETWORK_LAYER_SIZE[i]; n++){

                String current = p.getValue(new String[] { "Network", "Layers", new String(i + ""), "weights" }, ""+n);
                double[] val = ParserTools.parseDoubleArray(current);

                ne.weights[i][n] = val;
            }
        }
        p.close();
        return ne;

    }
}
