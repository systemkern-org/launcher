package systemkern.systemkern.hermes

import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.learning.config.Nesterovs
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class NeuralNetworkImplementation(
    activation: Activation,
    argumentToLayers: Int,
    private var conf : MultiLayerConfiguration?,
    private var log : Logger = LoggerFactory.getLogger(NeuralNetworkImplementation::class.java),
    var numEpochs : Int,
    var model : MultiLayerNetwork?,
    val rngSeed : Int,
    val rate : Double /* 0.0015 -- learning rate*/ ,
    val numRows : Int,
    val numColumns : Int,
    val outputNum : Int) {

    fun configure(){
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

    fun createMultilayerNeuralNetwork(){
        model = MultiLayerNetwork(conf)
        model?.init()
        //model.setListeners(ScoreIterationListener(5))  //print the score with every iteration
    }

    fun trainMNN(examples : INDArray,labels : INDArray){
        log.info("Train model....")
        for (i in 0..numEpochs.minus(1)) {
            log.info("Epoch $i")
            model?.fit(examples,labels)
        }
    }
}
