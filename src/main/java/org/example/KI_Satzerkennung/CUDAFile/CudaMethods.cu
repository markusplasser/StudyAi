extern "C"

 #define max(a,b) \
   ({ __typeof__ (a) _a = (a); \
       __typeof__ (b) _b = (b); \
     _a > _b ? _a : _b; })

__global__ void forward(double *output,int lenOutput
                        double *output_derivative, int lenOutDeriv
                        double *weights,int lenWeights
                        double *bias,int lenBias
                        int *layerSizes,int lenLayerSizes

                        int current_Layer,
                        int prev_Layer,
) {
    int neuron = blockIdx.x * blockDim.x + threadIdx.x;

    if(neuron >= layerSizes[current_Layer]){return;}
    int MAX_LAYER_SIZE = 0;
    for(int i = 0; i < lenLayerSizes;i++){
        max(MAX_LAYER_SIZE,layerSizes[i]);
    }
    
    double sum = bias[current_Layer * MAX_LAYER_SIZE + neuron];



     for(int i = 0; i < layerSizes[current_Layer-1]; i++){
        sum += weights[current_Layer * MAX_LAYER_SIZE * MAX_LAYER_SIZE + i] * output[(current_Layer-1)*MAX_LAYER_SIZE + i];
     }

     output[current_Layer * MAX_LAYER_SIZE + neuron] = sigmoid(sum);
     output_derivative[current_Layer * MAX_LAYER_SIZE + neuron] = output[current_Layer * MAX_LAYER_SIZE + neuron] * (1-(output[current_Layer * MAX_LAYER_SIZE + neuron]));
}


__device__ double sigmoid(double x){
    return 1/(1.0+exp(-x);
}

