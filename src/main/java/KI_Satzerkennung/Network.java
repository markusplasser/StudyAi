package KI_Satzerkennung;
import java.util.Random;

public class Network {
    //indexing weight : weigths[layer][neuron][prev neuron]
    private double[][][] weigths;
    private double[][] bias;
    private double[][] output;
    private double[][] output_derivative;
    private double[][] err_signal;

    private int NETWORK_SIZE;
    private int[] NETWORK_LAYER_SIZE;
    private int INPUT_LAYER_SIZE;
    private int OUTPUT_LAYER_SIZE;

    public Network(int... NETWORK_LAYER_SIZE) {
        NETWORK_SIZE = NETWORK_LAYER_SIZE.length;
        this.NETWORK_LAYER_SIZE = NETWORK_LAYER_SIZE;
        INPUT_LAYER_SIZE = NETWORK_LAYER_SIZE[0];
        OUTPUT_LAYER_SIZE = NETWORK_LAYER_SIZE[NETWORK_SIZE-1];

        weigths = new double[NETWORK_SIZE][][];
        bias = new double[NETWORK_SIZE][];
        output = new double[NETWORK_SIZE][];
        err_signal = new double[NETWORK_SIZE][];
        output_derivative = new double[NETWORK_SIZE][];

        for(int i = 0; i < NETWORK_SIZE; i++) {
            bias[i] = new double[NETWORK_LAYER_SIZE[i]];
            output[i] = new double[NETWORK_LAYER_SIZE[i]];
            err_signal[i] = new double[NETWORK_LAYER_SIZE[i]];
            output_derivative[i] = new double[NETWORK_LAYER_SIZE[i]];
            if(i > 0){
                weigths[i] = createRandomArr(NETWORK_LAYER_SIZE[i],NETWORK_LAYER_SIZE[i-1],-0.5,0.5);
            }
        }
    }



    /**
     * -Markus
     * Feedforward process...
     * SUM = bias + (weight[layer][neuron][prevNeuron] * output[layer][prevNeuron] + weight[layer][neuron][prevNeuron+1])
     * @param input
     * @return double[]
     */
    public double[] calculate(int... input){
        if(input.length != INPUT_LAYER_SIZE){
            return null;
        }
        for(int layer = 1; layer < NETWORK_SIZE; layer++){
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZE[layer]; neuron++){
                double sum = bias[layer][neuron];

                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZE[layer]; prevNeuron++){
                    sum += weigths[layer][neuron][prevNeuron] * output[layer][prevNeuron];
                }
                output[layer][neuron] = sum;
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
    public double[][] backpropagation(double[] traget){
        /**
         * err_signal = (outputVal - target) * output_derivetive[][]
         *
         * err_signal : wie viel Schuld hast du an dem Falschen output
         * output_derivetive -> wie stark ändert sich das Ergebnis bei kleinen Änderungen
         * je kleiner desto weniger hat es einfluss auf die änderung
         */
        for(int neuron = 0; neuron < OUTPUT_LAYER_SIZE; neuron++){
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
                    sum += weigths[layer][nextneuron][neuron] * err_signal[layer+1][nextneuron];
                }
                // Die Schuld ist die Summe aller weights die von dem Neuron ausgehen * ihre Wichtigkeit
                //Die Schuld von dem Neuron = summe der Ganzen schuld * die wichtigkeit die es hatte
                this.err_signal[layer][neuron] = sum* output_derivative[layer][neuron];
            }
        }
        return err_signal;
    }

    public void update(double eta){

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
     * -Markus
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
     * -Markus
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
}
