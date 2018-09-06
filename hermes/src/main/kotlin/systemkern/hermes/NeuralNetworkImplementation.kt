package systemkern.systemkern.hermes

import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.learning.config.Nesterovs
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger


class NeuralNetworkImplementation(
    private val numEpochs : Int, // learning rate*/
    inputRows : Int,
    outputNum : Int) {

    private var log : Logger = getLogger(NeuralNetworkImplementation::class.java)
    private var model : MultiLayerNetwork? = null
    private val rngSeed : Int = 123
    private val rate : Double = 0.2

    init {
        model = MultiLayerNetwork(
        NeuralNetConfiguration.Builder()
        .seed(rngSeed.toLong()) //include a random seed for reproducibility
        // use stochastic gradient descent as an optimization algorithm

        .activation(Activation.RELU)
        .weightInit(WeightInit.XAVIER)
        .updater(Nesterovs(rate, 0.98)) //specify the rate of change of the learning rate.
        .l2(rate) // regularize learning model
        .list()
        .layer(0, DenseLayer.Builder() //create the first input layer.
            .nIn(inputRows)
            .nOut(30)
            .build())
        .layer(1, DenseLayer.Builder() //create the second input layer
            .nIn(30)
            .nOut(15)
            .build())
        .layer(2, OutputLayer.Builder(NEGATIVELOGLIKELIHOOD) //create hidden layer
            .activation(Activation.SOFTMAX)
            .nIn(15)
            .nOut(outputNum)
            .build())
        .pretrain(false).backprop(true) //use backpropagation to adjust weights
        .build())
        model?.init()
    }

    fun trainMNN(examples : INDArray,labels : INDArray){
        log.info("Train model....")
        for (i in 0..numEpochs.minus(1)) {
            log.info("Epoch $i")
            model?.fit(examples,labels)
        }
    }

    fun predict(sentenceToVector : INDArray) : IntArray? {
        return model?.predict(sentenceToVector)
    }
}
