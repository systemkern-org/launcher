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
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CliEntryPoint::class])
internal class EchoControllerIT {

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
            .apply<DefaultMockMvcBuilder>(
                documentationConfiguration(this.restDocumentation)
                    .operationPreprocessors()
                    .withResponseDefaults(Preprocessors.prettyPrint())
            )
            .build()
    }

    @Test fun `Can Post Echo Request`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/default/echo")
            .content(objectMapper.writeValueAsString(
                EchoDTO(
                    value = "foo",
                    id = 1
                )
            ))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andDo {
                println(it.response.contentAsString)
            }
            .andExpect(status().isOk)
            .andDo(document("echo",
                requestFields(
                    fieldWithPath("id").description("id of the request sent").type(NUMBER),
                    fieldWithPath("value").description("some arbitrary end2end value").type(STRING),
                    fieldWithPath("timestamp").description("timestamp of the request").type(ARRAY)
                ),
                responseFields(
                    fieldWithPath("id").description("id of the request sent").type(NUMBER),
                    fieldWithPath("value").description("some arbitrary end2end value").type(STRING),
                    fieldWithPath("timestamp").description("timestamp of the request").type(ARRAY)
                )
            ))
    }

}


internal data class EchoDTO(
    val id: Int,
    val value: String,
    val timestamp: LocalDateTime? = LocalDateTime.now()
)
