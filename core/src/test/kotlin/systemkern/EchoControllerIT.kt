package systemkern

import org.junit.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@EnableAutoConfiguration
internal class EchoControllerIT : IntegrationTest() {

    @Test fun `Can Post Echo Request`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/default/echo")
            .content(objectMapper.writeValueAsString(
                EchoDTO(
                    id = 1,
                    value = "foo"
                )
            ))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("echo",
                responseFields(
                    fieldWithPath("id").description("Id of the request sent").type(NUMBER),
                    fieldWithPath("value").description("Some arbitrary end2end value").type(STRING),
                    fieldWithPath("timestamp").description("Time of the request").type(STRING)
                )
            ))
    }

}

private data class EchoDTO(
    val id: Int,
    val value: String,
    val timestamp: LocalDateTime? = LocalDateTime.now()
)
