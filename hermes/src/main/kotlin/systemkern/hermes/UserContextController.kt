package systemkern.hermes

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.stereotype.Component
import systemkern.hermes.StateMachineConfiguration.Companion.contextIdentifier
import java.util.HashMap

@Component
internal class UserContextController(
    @Autowired
    private val factory : StateMachineFactory<States, Events>,
    private val userMap : HashMap<Int, StateMachine<States, Events>> = HashMap()){

    fun checkUserId(userId : Int) : Boolean
        = userMap.size == 0 || !userMap.contains(userId)

    fun createStateForUser(userId : Int) {
        val stateMachine = factory.stateMachine
        val context = Context()
        context.userId = userId
        stateMachine.extendedState.variables[contextIdentifier] = context
        stateMachine.start()

        userMap [userId] = stateMachine
    }

    fun isEventPossible(userId : Int,event : Events) : Boolean {
        val stateMachine = userMap[userId]
        return stateMachine!!.transitions
            .filter { transition -> transition.source == stateMachine.state }
            .any { transition -> transition.trigger.event == event }
    }

    fun sendEvent(userId : Int, event: Events) {
        val stateMachine = userMap[userId]
        if (isEventPossible(userId, event)) {
            stateMachine!!.sendEvent(event)
        }
    }
}