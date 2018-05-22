package systemkern


import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CliEntryPoint::class])
internal class PingControllerIT {

    private val restDocumentation = JUnitRestDocumentation()
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    @Autowired
    private lateinit var context: WebApplicationContext
    private lateinit var mockMvc: MockMvc
    @get:Rule
    val rules = RuleChain
        .outerRule(restDocumentation)

    @Before
    fun setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(this.restDocumentation))
            .build()
    }

    @Test fun `Can Ping Application`() {
        this.mockMvc.perform(get("/default/ping").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("ping", responseFields(
                fieldWithPath("timestamp").description("return timestamp")
            )))
    }

}
