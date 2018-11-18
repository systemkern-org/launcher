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

    }

}
