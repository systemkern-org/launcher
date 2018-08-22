package systemkern.systemkern.hermes

import org.slf4j.LoggerFactory
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.action.Action
import org.springframework.stereotype.Component
import systemkern.hermes.Events
import systemkern.hermes.StateMachineConfiguration
import systemkern.hermes.States


@Component
internal class InitializeAction : Action<States, Events> {
    private val log = LoggerFactory.getLogger(StateMachineConfiguration::class.java)
    override fun execute(context: StateContext<States, Events>?) {
        log.info("initialize()")
    }

}
