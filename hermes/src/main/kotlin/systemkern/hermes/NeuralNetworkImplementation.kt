package systemkern.systemkern.hermes

import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Nesterovs
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import systemkern.Intent
import systemkern.NaturalLanguagePreProcessor
import java.io.File
import java.util.Random


class NeuralNetworkImplementation(
    private val numEpochs : Int, // learning rate*/
    private val nlpProcessor : NaturalLanguagePreProcessor,
    internal var intent : Intent? = null,
    private var log : Logger = getLogger(NeuralNetworkImplementation::class.java),
    private var model : MultiLayerNetwork? = null,
    rngSeed : Int = 12345,
    rate : Double = 0.0015,
    private val savedModelPath : String = "",
    private val randomGenerator : Random = Random() ) {

    init {
        model = MultiLayerNetwork(
        NeuralNetConfiguration.Builder()
        .seed(rngSeed.toLong()) //include a random seed for reproducibility
        // use stochastic gradient descent as an optimization algorithm

        .activation(Activation.RELU)
        .weightInit(WeightInit.XAVIER)
        .updater(Nesterovs(rate, 0.000001)) //specify the rate of change of the learning rate.
        .l2(rate * 0.005) // regularize learning model
        .list()
        .layer(0, DenseLayer.Builder() //create the first input layer.
            .nIn(nlpProcessor.words.size)
            .nOut(30)
            .build())
        .layer(1, DenseLayer.Builder() //create the second input layer
            .nIn(30)
            .nOut(15)
            .build())
        .layer(2, OutputLayer.Builder(NEGATIVELOGLIKELIHOOD) //create hidden layer
            .activation(Activation.SOFTMAX)
            .nIn(15)
            .nOut(nlpProcessor.classes.size)
            .build())
        .pretrain(false).backprop(true) //use backpropagation to adjust weights
        .build())
        model?.init()
    }

    fun trainMNN(){
        log.info("Train model....")
        for (i in 0..numEpochs.minus(1)) {
            for (j in 0..nlpProcessor.bags.size.minus(1)) {
                log.info("Epoch: $j")
                model?.fit( Nd4j.create(nlpProcessor.bags[j]), Nd4j.create(nlpProcessor.classesIntoArrays[j]))
            }
        }
    }

    fun answerToMessage(messageReceived : String) : String{
        val contextTag = model?.predict(Nd4j.create(nlpProcessor.sentence2array(messageReceived)))
        intent = nlpProcessor.intentList!![contextTag!![0]]
        val bound = intent!!.responses.size.minus(1)
        return intent!!.responses[ randomGenerator.nextInt( if((bound) > 0) bound else 1 ) ]
    }

    fun saveTrainedModel(){

        log.info("SAVE TRAINED MODEL")
        val locationToSave = File("$savedModelPath/trained_model.zip")
        // ModelSerializer needs model, saveUpdater, Location
        ModelSerializer.writeModel(model as MultiLayerNetwork, locationToSave, false)

    }
}
