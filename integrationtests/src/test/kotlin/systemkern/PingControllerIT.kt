package systemkern


import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@ActiveProfiles("integrationtest")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CliEntryPoint::class])
internal class PingControllerIT : IntegrationTest() {

    @Test fun `Can Ping Application`() {
        this.mockMvc.perform(get("/default/ping").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("ping", PayloadDocumentation.responseFields(
                PayloadDocumentation.fieldWithPath("timestamp").description("return timestamp")
            )))
    }

}
