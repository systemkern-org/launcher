package systemkern

import com.kotlinnlp.simplednn.core.functionalities.activations.Softmax
import com.kotlinnlp.simplednn.core.functionalities.activations.Softsign
import com.kotlinnlp.simplednn.core.layers.LayerType
import com.kotlinnlp.simplednn.core.neuralnetwork.preset.FeedforwardNeuralNetwork

/**
 * Create a fully connected neural network with an input layer with dropout,
 * two hidden layers with ELU activation function
 * and an output one with Softmax activation for classification purpose.
 */
fun initialize(){

    //TODO: Maybe it is not going to work with this library, delve how to do it with Smile
    val neuralNetwork = FeedforwardNeuralNetwork(
        inputSize = 356425,
        inputType = LayerType.Input.SparseBinary,
        hiddenSize = 200,
        hiddenActivation = Softsign(),
        outputSize = 86,
        outputActivation = Softmax())
}
