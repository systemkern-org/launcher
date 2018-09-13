package systemkern.hermes

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.support.DefaultStateMachineContext
import org.springframework.test.context.junit4.SpringRunner
import java.time.ZonedDateTime.now

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [StateMachineConfiguration::class])
internal class StateMachineIT {

    @Autowired
    private lateinit var stateMachineFactory: StateMachineFactory<States, Events>
    private lateinit var stateMachine: StateMachine<States, Events>

    @Before
    fun setup() {
        stateMachine = stateMachineFactory.stateMachine
        stateMachine.context = Context(mySessionInfo = "Foo Info")

    }


    @Test fun `Can do happy flow transitions`() {
        stateMachine.start()
        assertThat(stateMachine.context.mySessionInfo).isEqualToIgnoringCase("Foo Info")
        assertThat(stateMachine.context.start).isBeforeOrEqualTo(now())
        assertThat(stateMachine.context.jokesSent).isEqualTo(0)

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
        assertThat(stateMachine.isComplete).isTrue()
    }


    // https://youtu.be/M4Aa45Gpc4w?t=40m minute 41
    @Test fun `Can continue from persisted context`() {
        val persistedContext =
getFromRepo*
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
    }
}