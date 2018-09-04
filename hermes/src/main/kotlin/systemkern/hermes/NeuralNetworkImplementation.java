package systemkern.hermes;

import smile.regression.NeuralNetwork;

public class NeuralNetworkImplementation {

    private NeuralNetwork neuralNetwork;


    public NeuralNetworkImplementation(NeuralNetwork.ActivationFunction activation,int argumentToLayers) {
        neuralNetwork = new NeuralNetwork(activation,new int[]{ argumentToLayers, 10, 10,1 });
    }

    public void learn(double[][] outputRow,double[] bag){
        neuralNetwork.learn(outputRow, bag);
    }

    public double[] predict(double[][] input){
        return neuralNetwork.predict(input);
    }


}
