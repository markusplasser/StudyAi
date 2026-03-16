package KI_Satzerkennung;

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

    public TrainSet extractBatch(int ammount){
        TrainSet ret = new TrainSet(inputLength,outputLength);
        ArrayList<Integer> check = new ArrayList<>();
        Random ran = new Random();
        for(int i = 0; i<ammount;i++){
            int r = ran.nextInt(data.size());
            if(check.contains(r)){
                i--;
            }
            else{
                ret.add(getInput(r),getTarget(i));
                check.add(r);
            }
        }
        return ret;
    }

}
