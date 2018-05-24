package systemkern

/*import org.json.JSONObject*/
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

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CliEntryPoint::class])
internal class UserControllerIT : IntegrationTest() {

    var idUserCreated: String = ""
    @Test
    fun `Create user request`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/users")
            .content(objectMapper.writeValueAsString(
                UserDTO(
                    name = "Andres Test 01",
                    password = "AndresTest2018*01"
                )
            ))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andDo {
                println(it.response.contentAsString)
            }
            .andExpect(status().isOk)
            .andDo(document("user_create",
                requestFields(
                    fieldWithPath("name").description("name used by user").type(STRING),
                    fieldWithPath("password").description("password to access account").type(STRING)
                )/*,
                responseFields(
                    fieldWithPath("id").description("id of the user inserted").type(NUMBER),
                    fieldWithPath("name").description("name of the user").type(STRING),
                    fieldWithPath("password").description("password to access account").type(STRING)
                )*/
            ))

    }

    @Test
    fun `Read users request`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/users/1")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andDo {
                println(it.response.contentAsString)
            }
            .andExpect(status().isOk)
            .andDo(document("user_read",
                responseFields(
                    fieldWithPath("name").description("name of the user requested").type(STRING),
                    fieldWithPath("password").description("password of the user requested").type(STRING)
                )
            ))
    }

    @Test
    fun `Update users request`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/users/1")
            .content(objectMapper.writeValueAsString(
                UserDTO(
                    name = "AndresTest",
                    password = "AndresTest2018"
                )
            ))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andDo {
                println(it.response.contentAsString)
            }
            .andExpect(status().isOk)
            .andDo(document("user_update",
                requestFields(

                    fieldWithPath("name").description("name to update").type(STRING),
                    fieldWithPath("password").description("password to update").type(STRING)
                )
            ))
    }

    @Test
    fun `Delete users request`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/users/42")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andDo {
                println(it.response.contentAsString)
            }
            .andExpect(status().isOk)
            .andDo(document("user_delete"
            ))
    }
}

data class UserDTO(
    val name: String,
    val password: String
)
