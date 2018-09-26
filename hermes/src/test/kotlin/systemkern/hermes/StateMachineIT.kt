package systemkern.hermes

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [StateMachineConfiguration::class])
@ComponentScan(basePackages = ["systemkern"])
@EnableAutoConfiguration
internal class StateMachineIT {

    private val userId : Int = 123

    @Autowired
    lateinit var context: WebApplicationContext
    lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var nnImpl : NeuralNetworkImplementation
    @Autowired
    private lateinit var userContextController: UserContextController

    @Before
    fun setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .build()
    }

    private fun generateAnswerNnImpl(message : String){
        nnImpl.generateAnswerToMessage(message)
        userContextController.sendEvent(userId, nnImpl.contextTag!![0])
    }

    @Test fun `Can do happy flow transitions`() {
        userContextController.createStateForUser(userId)

        generateAnswerNnImpl("hi")
        assertThat(userContextController
            .getStateByUserId(userId))
            .isEqualTo(States.GREETING_RECEIVED)

        generateAnswerNnImpl("hours are you open?")
        assertThat(userContextController
            .getStateByUserId(userId))
            .isEqualTo(States.GENERAL_INFO_REQUESTED)
        /*

        // this is triggered by input from the user / external events
        // for example: user says "Hey! send me a joke"
        // the Natural Language Processor (NLP) processes this and sends the correct event to the state machine
        stateMachine.sendEvent(Events.RECEIVE_JOKE_REQUEST)
        // assert that state machine transition was triggered
        assertThat(stateMachine.state.id).isEqualTo(States.JOKE_REQUESTED)
        assertThat(stateMachine.context.jokesSent).isEqualTo(0)

        // action is to ask for confirmation "Do you really want a joke?"
        // user answers with for example: "yes"
        stateMachine.sendEvent(Events.RECEIVE_CONFIRMATION)
        // so we got the first confirmation

        // this will trigger a new action an
        // and we should land in States Step 2
        assertThat(stateMachine.state.id).isEqualTo(States.REQUEST_CONFIRMED)
        assertThat(stateMachine.context.jokesSent).isEqualTo(0)

        // the action was to ask the user for a payment
        // user answers again with for example: "100$"
        stateMachine.sendEvent(Events.RECEIVE_PAYMENT)
        // so we got the second payment
        // this will trigger the sending of the joke
        // and we should land in the done state
        assertThat(stateMachine.state.id).isEqualTo(States.DONE)
        // check that the joke has been sent now
        assertThat(stateMachine.context.jokesSent).isEqualTo(1)

        assertThat(stateMachine.state.id).isEqualTo(States.DONE)
        assertThat(stateMachine.isComplete).isTrue()*/
    }


    // https://youtu.be/M4Aa45Gpc4w?t=40m minute 41
    /*@Test fun `Can continue from persisted context`() {
        val persistedContext =
getFromRepo
 Context(state = States.REQUEST_CONFIRMED)

        stateMachine.stop()
        stateMachine.stateMachineAccessor.doWithAllRegions {
            it.resetStateMachine(DefaultStateMachineContext(
                persistedContext.state, null, null, null
            ))
        }
        stateMachine.start()


        // so we got the first confirmation

        // this will trigger a new action an
        // and we should land in States Step 2
        assertThat(stateMachine.state.id).isEqualTo(States.REQUEST_CONFIRMED)
        assertThat(stateMachine.context.jokesSent).isEqualTo(0)

        // the action was to ask the user for a payment
        // user answers again with for example: "100$"
        stateMachine.sendEvent(Events.RECEIVE_PAYMENT)
        // so we got the second payment
        // this will trigger the sending of the joke
        // and we should land in the done state
        assertThat(stateMachine.state.id).isEqualTo(States.DONE)
        // check that the joke has been sent now
        assertThat(stateMachine.context.jokesSent).isEqualTo(1)

        assertThat(stateMachine.state.id).isEqualTo(States.DONE)
        assertThat(stateMachine.isComplete).isTrue()
    }*/
}
