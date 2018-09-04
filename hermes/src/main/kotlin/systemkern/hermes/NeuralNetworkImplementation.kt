package systemkern.systemkern.hermes

import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.Nesterovs
import org.nd4j.linalg.lossfunctions.LossFunctions


class NeuralNetworkImplementation(
    activation: Activation,
    argumentToLayers: Int,
    private var conf : MultiLayerConfiguration) {

    fun configure(
        rngSeed : Int,
        rate : Double /* 0.0015 -- learning rate*/ ,
        numRows : Int,
        numColumns : Int,
        outputNum : Int){

        conf = NeuralNetConfiguration.Builder()
        .seed(rngSeed.toLong()) //include a random seed for reproducibility
        // use stochastic gradient descent as an optimization algorithm

        .activation(Activation.RELU)
        .weightInit(WeightInit.XAVIER)
        .updater(Nesterovs(rate, 0.98)) //specify the rate of change of the learning rate.
        .l2(rate * 0.005) // regularize learning model
        .list()
        .layer(0, DenseLayer.Builder() //create the first input layer.
            .nIn(numRows * numColumns)
            .nOut(500)
            .build())
        .layer(1, DenseLayer.Builder() //create the second input layer
            .nIn(500)
            .nOut(100)
            .build())
        .layer(2, OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
            .activation(Activation.SOFTMAX)
            .nIn(100)
            .nOut(outputNum)
            .build())
        .pretrain(false).backprop(true) //use backpropagation to adjust weights
        .build()
    }
}
