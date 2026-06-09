package org.example.KI_Satzerkennung;

import java.util.ArrayList;
import java.util.Random;

public class TrainSet {
    public final int inputLength;
    public final int outputLength;

    private ArrayList<double[][]> data = new ArrayList<>();

    public TrainSet(int inputLength,int outputLength){
        this.inputLength = inputLength;
        this.outputLength = outputLength;
    }

    /**
     * adds a data point to the set
     * @param input input
     * @param target target
     * @return true if and only if adding was successful
     */
    public boolean add(double[] input, double[] target){
        if(input.length != inputLength || target.length != outputLength){
            return false;
        }
        data.add(new double[][]{input,target});
        return true;
    }

    public double[] getInput(int index){
        return data.get(index)[0];
    }
    public double[] getTarget(int index){
        return data.get(index)[1];
    }

    /**
     * extracts a small amount of data points from a trainset
     * @param amount amount
     * @return trainset
     */
    public TrainSet extractBatch(int amount){
        TrainSet ret = new TrainSet(inputLength,outputLength);
        ArrayList<Integer> check = new ArrayList<>();
        Random ran = new Random();
        for(int i = 0; i<amount;i++){
            int r = ran.nextInt(data.size());
            if(check.contains(r)){
                i--;
            }
            else{
                ret.add(getInput(r),getTarget(r));
                check.add(r);
            }
        }
        return ret;
    }

    public int size(){
        return data.size();
    }

}
