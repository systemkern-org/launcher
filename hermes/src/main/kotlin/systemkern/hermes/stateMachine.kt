package systemkern.hermes

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import org.springframework.statemachine.listener.StateMachineListener
import org.springframework.statemachine.state.State
import org.springframework.statemachine.transition.Transition
import org.springframework.stereotype.Component
import java.lang.Exception
import java.util.*

internal enum class Events {
    RECEIVE_GREETING,
    RECEIVE_GOODBYE,
    RECEIVE_THANKS,
    REQUEST_RENT,
    REQUEST_GENERAL_INFO
}

internal enum class States {
    GREETING_RECEIVED,
    GOODBYE_RECEIVED,
    RENT_REQUESTED,
    GENERAL_INFO_REQUESTED,
    THANKS_RECEIVED
}

internal class Context {
    var userId = 0
}

@Configuration
@ComponentScan(basePackages = ["systemkern.hermes"])
@EnableStateMachineFactory
internal class StateMachineConfiguration(
    private val listener: StateMachineListener<States, Events>
) : EnumStateMachineConfigurerAdapter<States, Events>() {

    companion object {
        internal const val contextIdentifier = "context"
    }

    //private val log = LoggerFactory.getLogger(StateMachineConfiguration::class.java)

    override fun configure(config: StateMachineConfigBuilder<States, Events>) {

    }

    override fun configure(config: StateMachineConfigurationConfigurer<States, Events>) {
        config.withConfiguration()
            .autoStartup(true)
            .listener(listener)
    }

    override fun configure(states: StateMachineStateConfigurer<States, Events>) {
        states.withStates()
            .initial(States.GREETING_RECEIVED)
            .end(States.GOODBYE_RECEIVED)
            .end(States.THANKS_RECEIVED)
            .states(EnumSet.allOf(States::class.java))
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<States, Events>) {
        transitions.withExternal()
            // Initial state
            .source(States.GREETING_RECEIVED) // STATE IN WHICH STATE MACHINE IS LOCATED RIGHT NOW
            .event(Events.REQUEST_RENT)
            .target(States.RENT_REQUESTED)

            .and().withExternal()
            .source(States.GREETING_RECEIVED)
            .event(Events.REQUEST_GENERAL_INFO)
            .target(States.GENERAL_INFO_REQUESTED)

            // General info state
            .and().withExternal()
            .source(States.GENERAL_INFO_REQUESTED)
            .event(Events.RECEIVE_GOODBYE)

            .and().withExternal()
            .source(States.GENERAL_INFO_REQUESTED)
            .event(Events.RECEIVE_THANKS)

            // Rent state
            .and().withExternal()
            .source(States.RENT_REQUESTED)
            .event(Events.RECEIVE_THANKS)

    }

}

@Component
internal class Listener : StateMachineListener<States, Events> {
    private val log = LoggerFactory.getLogger(StateMachineConfiguration::class.java)

    override fun stateMachineStopped(stateMachine: StateMachine<States, Events>?) {}

    override fun extendedStateChanged(key: Any?, value: Any?) {}

    override fun stateEntered(state: State<States, Events>?) {}

    override fun eventNotAccepted(event: Message<Events>?) {}

    override fun stateMachineStarted(stateMachine: StateMachine<States, Events>?) {}

    override fun stateContext(stateContext: StateContext<States, Events>?) {}

    override fun stateChanged(from: State<States, Events>?, to: State<States, Events>?) {
        log.info("Listening to entry of state $to")
    }

    override fun transition(transition: Transition<States, Events>?) {}

    override fun transitionEnded(transition: Transition<States, Events>?) {}

    override fun transitionStarted(transition: Transition<States, Events>?) {}

    override fun stateMachineError(stateMachine: StateMachine<States, Events>?, exception: Exception?) {}

    override fun stateExited(state: State<States, Events>?) {}

}