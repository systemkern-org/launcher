package systemkern.hermes


import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.action.Action
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
import systemkern.systemkern.hermes.InitializeAction
import java.lang.Exception
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now
import java.util.*

internal enum class Events {
    ABORT,
    RECEIVE_JOKE_REQUEST,
    RECEIVE_CONFIRMATION,
    RECEIVE_PAYMENT,
    USER_ABORTS
}

internal enum class States {
    INITIAL,
    JOKE_REQUESTED,
    REQUEST_CONFIRMED,
    DONE
}


internal data class Context(
    val start: ZonedDateTime = now(),
    var state: States = States.INITIAL,
    var jokesSent: Int = 0,
    var mySessionInfo: String = ""
)

private const val contextIdentifier = "context"
/** This extension property provides easy access our custom Context data storage */
internal var StateMachine<States, Events>.context: Context
    get() = (this.extendedState.variables[contextIdentifier] as Context?)
        ?: Context()
    set(value) {
        this.extendedState.variables[contextIdentifier] = value
    }

/** This extension property provides easy access our custom Context data storage */
internal var StateContext<States, Events>.context: Context
    get() = (this.extendedState.variables[contextIdentifier] as Context?)
        ?: Context()
    set(value) {
        this.extendedState.variables[contextIdentifier] = value
    }


@Configuration
@ComponentScan(basePackages = ["systemkern.hermes"])
@EnableStateMachineFactory
internal class StateMachineConfiguration(
    private val listener: StateMachineListener<States, Events>
) : EnumStateMachineConfigurerAdapter<States, Events>() {

    private val log = LoggerFactory.getLogger(StateMachineConfiguration::class.java)

    override fun configure(config: StateMachineConfigBuilder<States, Events>) {

    }

    override fun configure(config: StateMachineConfigurationConfigurer<States, Events>) {
        config.withConfiguration()
            .autoStartup(true)
            .listener(listener)
    }

    override fun configure(states: StateMachineStateConfigurer<States, Events>) {
        states.withStates()
            .initial(States.INITIAL, initialize())
            .end(States.DONE)
            .states(EnumSet.allOf(States::class.java))

    }

    override fun configure(transitions: StateMachineTransitionConfigurer<States, Events>) {
        transitions.withExternal()
            // Joke Sending Path
            .source(States.INITIAL)
            .event(Events.RECEIVE_JOKE_REQUEST)
            .action(initialize())
            .target(States.JOKE_REQUESTED)

            .and().withExternal()
            .source(States.JOKE_REQUESTED)
            .event(Events.RECEIVE_CONFIRMATION)
            .action(askForSecondConfirmation())
            .target(States.REQUEST_CONFIRMED)

            .and().withExternal()
            .source(States.REQUEST_CONFIRMED)
            .event(Events.RECEIVE_PAYMENT)
            .target(States.DONE)
            .action(sendJoke())

            // Aborts for Joke Sending Path
            .and().withExternal()
            .source(States.JOKE_REQUESTED)
            .event(Events.USER_ABORTS)
            .action(userAborted())
            .target(States.DONE)

            .and().withExternal()
            .source(States.REQUEST_CONFIRMED)
            .event(Events.ABORT)
            .action(userAborted())
            .target(States.DONE)

    }

    @Bean fun initialize(): Action<States, Events> =
        InitializeAction()

    @Bean fun askForSecondConfirmation(): Action<States, Events> =
        Action { log.info("askForSecondConfirmation()") }

    @Bean fun askForPayment(): Action<States, Events> =
        Action { log.info("askForPayment()") }

    @Bean fun sendJoke(): Action<States, Events> =
        Action {
            it.context.jokesSent++
            log.info("sendJoke()")
        }

    @Bean fun userAborted(): Action<States, Events> =
        Action { log.info("userAborted()") }

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