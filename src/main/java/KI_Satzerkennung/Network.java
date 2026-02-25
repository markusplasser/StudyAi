package KI_Satzerkennung;
import java.util.Random;

public class Network {
    private double[][][] weigths;
    private double[][] bias;
    private double[][] output;

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

        for(int i = 0; i < NETWORK_SIZE; i++) {
            bias[i] = new double[NETWORK_LAYER_SIZE[i]];
            output[i] = new double[NETWORK_LAYER_SIZE[i]];
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
            }
        }
        return output[NETWORK_SIZE-1];
    }




    public double[][] backpropagation(){

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
