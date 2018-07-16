package systemkern

import org.junit.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@EnableAutoConfiguration
internal class PingControllerIT : IntegrationTest() {

    @Test fun `Can Ping Application`() {
        this.mockMvc.perform(get("/default/ping").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("ping",
                responseFields(
                    fieldWithPath("timestamp").description("return timestamp")
                )))
    }

}
