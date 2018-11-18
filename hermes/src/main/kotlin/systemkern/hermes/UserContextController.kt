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
    private val factory: StateMachineFactory<States, Events>,
    private val userMap: HashMap<Int, StateMachine<States, Events>> = HashMap(),
    private val events: HashMap<Int, Events> = HashMap()) {

    init {
        events[0] = Events.RECEIVE_GREETING
        events[1] = Events.RECEIVE_GOODBYE
        events[2] = Events.RECEIVE_THANKS
        events[3] = Events.REQUEST_GENERAL_INFO
        events[4] = Events.REQUEST_GENERAL_INFO
        events[5] = Events.REQUEST_GENERAL_INFO
        events[6] = Events.REQUEST_GENERAL_INFO
        events[7] = Events.REQUEST_GENERAL_INFO
        events[8] = Events.REQUEST_RENT
        events[9] = Events.REQUEST_GENERAL_INFO
    }

    fun checkUserId(userId: Int): Boolean = userMap.size == 0 || !userMap.contains(userId)

    fun createStateForUser(userId: Int) {
        val stateMachine = factory.stateMachine
        val context = Context()
        context.userId = userId
        stateMachine.extendedState.variables[contextIdentifier] = context
        stateMachine.start()

        userMap[userId] = stateMachine
    }

    fun getStateByUserId(userId: Int): States = userMap[userId]!!.state.id

    fun isEventPossible(userId: Int, event: Events): Boolean {
        val stateMachine = userMap[userId]
        return stateMachine!!.transitions
            .filter { transition -> transition.source == stateMachine.state }
            .any { transition -> transition.trigger.event == event }
    }

    fun sendEvent(userId: Int, tag: Int) {
        val stateMachine = userMap[userId]
        val event = events[tag]
        if (isEventPossible(userId, event as Events)) {
            stateMachine!!.sendEvent(event)
        }
    }
}