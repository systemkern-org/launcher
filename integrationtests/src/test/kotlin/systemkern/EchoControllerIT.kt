package systemkern


import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CliEntryPoint::class])
internal class EchoControllerIT : IntegrationTest() {

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
