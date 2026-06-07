package org.example.KI_Satzerkennung;
import java.util.Random;

import jcuda.CudaException;
import org.example.KI_Satzerkennung.parser.Attribute;
import org.example.KI_Satzerkennung.parser.Node;
import org.example.KI_Satzerkennung.parser.Parser;
import org.example.KI_Satzerkennung.parser.ParserTools;

//CUDA
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;
import jcuda.runtime.*;
import static jcuda.driver.JCudaDriver.*;

import java.util.Arrays;




public class Network {
    //indexing weight : weigths[layer][neuron][prev neuron]
    private double[][][] weights;
    private double[][] bias;
    private double[][] output;
    private double[][] output_derivative;
    private double[][] err_signal;
    private double[][] embedding;

    private static final double eta = 0.05;
    private final int EMBEDDING_DIM;
    private final int VOCAB_SIZE;
    private int NETWORK_SIZE;
    private int[] NETWORK_LAYER_SIZE;
    private int INPUT_LAYER_SIZE;
    private int OUTPUT_LAYER_SIZE;
    private int MAX_LAYER_SIZE;

    // CUDA Felder
    private CUfunction forwardFunction;
    private CUdeviceptr d_output;
    private CUdeviceptr d_outputDeriv;
    private CUdeviceptr d_weights;
    private CUdeviceptr d_bias;
    private CUdeviceptr d_layerSizes;

    /**
     * Constructor
     * @param VOCAB_SIZE vocab_size
     * @param embeddingDim embeddingDim
     * @param NETWORK_LAYER_SIZE networt_layer_size
     */
    public Network(int VOCAB_SIZE, int embeddingDim, int... NETWORK_LAYER_SIZE) {
        EMBEDDING_DIM = embeddingDim;
        this.VOCAB_SIZE = VOCAB_SIZE;
        NETWORK_SIZE = NETWORK_LAYER_SIZE.length;
        this.NETWORK_LAYER_SIZE = NETWORK_LAYER_SIZE;
        INPUT_LAYER_SIZE = NETWORK_LAYER_SIZE[0];
        OUTPUT_LAYER_SIZE = NETWORK_LAYER_SIZE[NETWORK_SIZE-1];

        weights = new double[NETWORK_SIZE][][];
        bias = new double[NETWORK_SIZE][];
        output = new double[NETWORK_SIZE][];
        err_signal = new double[NETWORK_SIZE][];
        output_derivative = new double[NETWORK_SIZE][];
        embedding = new double[this.VOCAB_SIZE][EMBEDDING_DIM];
        MAX_LAYER_SIZE = 0;

        for(int i = 0; i < NETWORK_SIZE; i++) {
            bias[i] = createRandomArr(NETWORK_LAYER_SIZE[i],-0.5,0.5);
            output[i] = new double[NETWORK_LAYER_SIZE[i]];
            err_signal[i] = new double[NETWORK_LAYER_SIZE[i]];
            output_derivative[i] = new double[NETWORK_LAYER_SIZE[i]];

            if(NETWORK_LAYER_SIZE[i] > MAX_LAYER_SIZE){
                MAX_LAYER_SIZE = NETWORK_LAYER_SIZE[i];
            }
            if(i > 0){
                weights[i] = createRandomArr(NETWORK_LAYER_SIZE[i],NETWORK_LAYER_SIZE[i-1],-0.5,0.5);
            }
        }
        for(int i = 0; i< 5000; i++){
            embedding[i] = createRandomArr(8,-0.1,0.1);
        }
    }



    /**
     * Feedforward algorithm
     * @param input double[] input
     * @return calculated double[]
     */
    public double[] calculate(double... input){
        if(input.length != INPUT_LAYER_SIZE){
            return null;
        }
        output[0] =input;
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
     * Embeds the Tokens in 8d vectors
     * @param rawIDs Tokens
     * @return embedded IDs
     */
    public double[] embedInput(double[] rawIDs){
        double[] ret = new double[rawIDs.length*EMBEDDING_DIM];
        int count = 0;
        for (double rawID : rawIDs) {
            int id = (int) rawID;
            double[] add = embedding[id];
            for (double v : add) {
                ret[count] = v;
                count++;
            }
        }
        return ret;
    }


     /**
      * Calculates the cost func.
      * <a href="https://www.youtube.com/watch?v=tIeHLnjs5U8">Video for explanation</a>
      * @param traget target value
      * @param rawIDs predicted output
      */
    public void backpropagation(double[] traget, double[] rawIDs){

//         err_signal = (outputVal - target) * output_derivetive[][]
//         err_signal : wie viel Schuld hast du an dem Falschen output
//         output_derivetive -> wie stark ändert sich das Ergebnis bei kleinen Änderungen
//         je kleiner desto weniger hat es einfluss auf die änderung

        for(int neuron = 0; neuron < OUTPUT_LAYER_SIZE; neuron++)
        {
            err_signal[NETWORK_SIZE-1][neuron] = (output[NETWORK_SIZE-1][neuron] - traget[neuron])
                    * output_derivative[NETWORK_SIZE-1][neuron];
        }


//         layer = NETWORK_SIZE-2 -> weil es mit dem Inedexing auf die weights sonst Probleme gibt
//         weights[layer][neuron][prev neuron]


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

        //Updates for the embedding
        for(int i = 0; i < rawIDs.length; i++){
            int tokenID = (int) rawIDs[i];

            if(tokenID == 0) continue;

            for(int dim = 0; dim < EMBEDDING_DIM; dim++){
                int inputIndex = i*EMBEDDING_DIM+dim;

                double fehler = 0;
                for(int neuron = 0; neuron < NETWORK_LAYER_SIZE[1]; neuron++){
                    fehler += err_signal[1][neuron] * weights[1][neuron][inputIndex];
                }

                embedding[tokenID][dim] -= fehler * eta;
            }
        }
    }

    /**
     * extracts batch and calls train with that Trainset
     * @param trainSet Trainset
     * @param batchsize batchsize
     * @param anz amount
     */
    public void train(TrainSet trainSet, int batchsize, int anz){
        for(int i = 0; i < anz; i++){
            TrainSet batch = trainSet.extractBatch(batchsize);
            for(int j = 0; j < batchsize; j++){
                train(batch.getInput(j),batch.getTarget(j));
                //System.out.println(Arrays.toString(batch.getInput(j)) + "\n" + Arrays.toString(batch.getTarget(j)));
            }
        }
    }

    /**
     * calls the right functions in the right order to train the network
     * @param rawIDs rawIDs
     * @param target target value
     */
    private void train(double[] rawIDs, double[] target){
        if(rawIDs.length* EMBEDDING_DIM != INPUT_LAYER_SIZE || target.length != OUTPUT_LAYER_SIZE){return;}
        double[] embedded = embedInput(rawIDs);
        calculate(embedded);
        backpropagation(target, rawIDs);
        update(eta);
    }

    /**
     * Checks one sentence on the CPU
     * @param input double[] input
     * @return double[] output of network
     */
    public double[] checkSentence(double[] input){
        return calculate(embedInput(input));
    }

    /**
     * Checks one sentence on the GPU
     * @param input double[] input
     * @return double[] output of network
     */
    public double[] checkSentenceGPU(double[] input){
        return calcGPU(embedInput(input));
    }

    /**
     * updates weights and bias with a factor of eta
     * @param eta double eta - factor
     */
    public void update(double eta){
        for(int layer = 1; layer < NETWORK_SIZE; layer++){
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZE[layer]; neuron++){
                for(int prevneuron = 0;  prevneuron < NETWORK_LAYER_SIZE[layer-1]; prevneuron++){
//                     -eta -> wollen den Fehler minimieren -> zum minimum der funk.
//                     output[layer-1][prevneuron] -> falls der output 0 war hat der weight keine Schuld am Fehler
//                     err_signal[layer][neuron] -> der Fehler dieses Neurons -> großer/kleiner Fehler viel/wenig anpassen
                    double delta = - eta * output[layer-1][prevneuron]* err_signal[layer][neuron];
                    weights[layer][neuron][prevneuron] += delta;
                }
//                 Das selbe wie bei den weights nur dass der bias addiert wird und desshalb immer schuld am Fehler hat
                double delta = -eta * err_signal[layer][neuron];
                bias[layer][neuron] += delta;
            }
        }
    }

    /**
     * Calculates the sigmoid function with a given value
     * @param val double value
     * @return sigmoid value
     */
    private double sigmoid(double val){
        return 1 / (1 + Math.exp(-val));
    }


    /**
     * Creates a new double[size][prevSize] and set values between lower_bound and upper_bound
     * @param size int size
     * @param prevSize int prevSize
     * @param lower_bound double lower_bound
     * @param upper_bound double upper_bound
     * @return random double[][]
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
     * @param size int size
     * @param lower_bound double lower_bound
     * @param upper_bound double upper_bound
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

    /**
     * saves a network current parameter in .txt file
     * generated
     * @param path String path
     */
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
        Node emb = new Node("Embeddings");
        root.addChild(emb);
        for(int i = 0; i < VOCAB_SIZE; i++){
            emb.addAttribute("" + i, Arrays.toString(embedding[i]));
        }
        p.close();
    }

    /**
     * loads a network from a save file
     * @param path String path
     * @return the loaded network
     */
    public static Network loadNetwork(String path)throws Exception{
        Parser p = new Parser();

        p.load(path);
        String sizes = p.getValue(new String[] { "Network" }, "sizes");
        int[] si = ParserTools.parseIntArray(sizes);
        Network ne = new Network(5000,8,si);

        for (int i = 1; i < ne.NETWORK_SIZE; i++) {
            String biases = p.getValue(new String[] { "Network", "Layers", i + "", "biases" }, "values");
            double[] bias = ParserTools.parseDoubleArray(biases);
            ne.bias[i] = bias;

            for(int n = 0; n < ne.NETWORK_LAYER_SIZE[i]; n++){

                String current = p.getValue(new String[] { "Network", "Layers", i + "", "weights" }, ""+n);
                double[] val = ParserTools.parseDoubleArray(current);

                ne.weights[i][n] = val;
            }
        }
        for(int i = 0; i < ne.VOCAB_SIZE; i++){
            String embRow = p.getValue(new String[]{"Embeddings"}, "" + i);
            ne.embedding[i] = ParserTools.parseDoubleArray(embRow);
        }
        p.close();
        return ne;
    }

    /**
     * Calc the feedforward algorithm on the GPU
     * Still slower than the CPU do to not efficient transfer of data between CPU und GPU
     * @param input double[] input
     * @return double[] output of network
     */
    public double[] calcGPU(double[] input) {
        // Input in output[0] setzen und flach kopieren
        output[0] = input;
        double[] flatOutput = flattenOutput();
        cuMemcpyHtoD(d_output, Pointer.to(flatOutput), (long) flatOutput.length * Sizeof.DOUBLE);

        // Skalare als Arrays für Pointer.to()
        int[] pLenOutput     = { flatOutput.length };
        int[] pLenOutDeriv   = { flatOutput.length }; // gleiche Größe wie output
        int[] pLenWeights    = { flattenWeights().length };
        int[] pLenBias       = { flattenBias().length };
        int[] pLenLayerSizes = { NETWORK_LAYER_SIZE.length };
        int[] pMaxLayerSize  = { MAX_LAYER_SIZE };

        for (int layer = 1; layer < NETWORK_SIZE; layer++) {
            int[] pCurrentLayer = { layer };
            int[] pPrevLayer    = { layer - 1 };

            Pointer kernelParams = Pointer.to(
                    Pointer.to(d_output),         Pointer.to(pLenOutput),
                    Pointer.to(d_outputDeriv),    Pointer.to(pLenOutDeriv),
                    Pointer.to(d_weights),        Pointer.to(pLenWeights),
                    Pointer.to(d_bias),           Pointer.to(pLenBias),
                    Pointer.to(d_layerSizes),     Pointer.to(pLenLayerSizes),
                    Pointer.to(pCurrentLayer),
                    Pointer.to(pPrevLayer),
                    Pointer.to(pMaxLayerSize)
            );

            int neurons   = NETWORK_LAYER_SIZE[layer];
            int threads   = 256;
            int blocks    = (neurons + threads - 1) / threads;
            int sharedMem = MAX_LAYER_SIZE * Sizeof.DOUBLE;

            cuLaunchKernel(forwardFunction,
                    blocks,  1, 1,
                    threads, 1, 1,
                    sharedMem, null,
                    kernelParams, null
            );
            cuCtxSynchronize();
        }

        // Ergebnis des letzten Layers zurückkopieren
        int lastLayerOffset = (NETWORK_SIZE - 1) * MAX_LAYER_SIZE;
        double[] result = new double[NETWORK_LAYER_SIZE[NETWORK_SIZE - 1]];
        cuMemcpyDtoH(
                Pointer.to(result),
                d_output.withByteOffset((long) lastLayerOffset * Sizeof.DOUBLE),
                (long) result.length * Sizeof.DOUBLE
        );
        return result;
    }


    /**
     * initializes the Cuda Kernel
     * only possible with Nvidia GPU's
     */
    public void initCUDA() throws Exception {

        //Throws Exceptions and doesn't die silent!! -important for debugging
        JCudaDriver.setExceptionsEnabled(true);
        cuInit(0);

        //Empty GPU device.
        //device gets set at index 0
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);

        //context is the connection to the usable space in the GPU
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        // load Kernel (.ptx File has to be Compiled - nvcc Compiler)
        CUmodule module = new CUmodule();
        cuModuleLoad(module, "src/main/java/org/example/KI_Satzerkennung/CUDAFile/forward.ptx");
        forwardFunction = new CUfunction();
        cuModuleGetFunction(forwardFunction, module, "forward");


        double[] flatWeights    = flattenWeights();
        double[] flatBias       = flattenBias();
        double[] flatOutput     = flattenOutput();
        double[] flatOutDeriv   = flattenOutputDeriv();

        // Speicher allokieren
        d_output     = new CUdeviceptr();
        d_outputDeriv= new CUdeviceptr();
        d_weights    = new CUdeviceptr();
        d_bias       = new CUdeviceptr();
        d_layerSizes = new CUdeviceptr();

        cuMemAlloc(d_output,      (long) flatOutput.length    * Sizeof.DOUBLE);
        cuMemAlloc(d_outputDeriv, (long) flatOutDeriv.length  * Sizeof.DOUBLE);
        cuMemAlloc(d_weights,     (long) flatWeights.length   * Sizeof.DOUBLE);
        cuMemAlloc(d_bias,        (long) flatBias.length      * Sizeof.DOUBLE);
        cuMemAlloc(d_layerSizes,  (long) NETWORK_LAYER_SIZE.length * Sizeof.INT);

        // Weights, Bias und LayerSizes einmalig kopieren (ändern sich nicht)
        cuMemcpyHtoD(d_weights,    Pointer.to(flatWeights),         (long) flatWeights.length  * Sizeof.DOUBLE);
        cuMemcpyHtoD(d_bias,       Pointer.to(flatBias),            (long) flatBias.length     * Sizeof.DOUBLE);
        cuMemcpyHtoD(d_layerSizes, Pointer.to(NETWORK_LAYER_SIZE),  (long) NETWORK_LAYER_SIZE.length * Sizeof.INT);
    }

    /**
     * Creates 1 dim arr out of double[][][] for weights
     * @return double[] flattened
     */
    private double[] flattenWeights() {
        // weights[layer][neuron][prevNeuron] -> [layer * MAX² + neuron * MAX + prevNeuron]
        double[] flat = new double[NETWORK_SIZE * MAX_LAYER_SIZE * MAX_LAYER_SIZE];
        for (int l = 1; l < NETWORK_SIZE; l++) {
            for (int n = 0; n < NETWORK_LAYER_SIZE[l]; n++) {
                for (int p = 0; p < NETWORK_LAYER_SIZE[l-1]; p++) {
                    flat[l * MAX_LAYER_SIZE * MAX_LAYER_SIZE + n * MAX_LAYER_SIZE + p] = weights[l][n][p];
                }
            }
        }
        return flat;
    }

    /**
     * Creates 1 dim arr out of double[][] for bias
     * @return double[] flattened
     */
    private double[] flattenBias() {
        return getDoubles(bias);
    }

    /**
     * Creates 1 dim arr out of double[][] for output
     * @return double[] flattened
     */
    private double[] flattenOutput() {
        return getDoubles(output);
    }

    /**
     * Creates 1 dim arr out of double[][] for OutputDeriv
     * @return double[] flattened
     */
    private double[] flattenOutputDeriv() {
        return getDoubles(output_derivative);
    }

    /**
     * helper method that actually creates the one dim arr
     * @param array double[][] array
     * @return flattened array
     */
    private double[] getDoubles(double[][] array) {
        double[] flat = new double[NETWORK_SIZE * MAX_LAYER_SIZE];
        for (int l = 0; l < NETWORK_SIZE; l++) {
            if (NETWORK_LAYER_SIZE[l] >= 0)
                System.arraycopy(array[l], 0, flat, l * MAX_LAYER_SIZE, NETWORK_LAYER_SIZE[l]);
        }
        return flat;
    }
}
