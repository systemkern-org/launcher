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

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CliEntryPoint::class])
internal class UserControllerIT : IntegrationTest() {

    @Test
    fun `Create user request`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/users")
            .content(objectMapper.writeValueAsString(
                UserDTO(
                    name = "Test user to create",
                    password = "*TestUser2018*"
                )
            ))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isCreated)
            .andDo(document("user_create",
                requestFields(
                    fieldWithPath("name").description("Name of user to be created").type(STRING),
                    fieldWithPath("password").description("Password of user to be created").type(STRING)
                ),
                responseFields(
                    fieldWithPath("name").description("Name of the user").type(STRING),
                    fieldWithPath("password").description("Password to access account").type(STRING),
                    fieldWithPath("_links.self.href").description("Link to access the created user").type(STRING),
                    fieldWithPath("_links.user.href").description("Link to access the created user").type(STRING)
                )
            ))

    }

    @Test
    fun `Read users request`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/users/1")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_read",
                responseFields(
                    fieldWithPath("name").description("Name of the user requested").type(STRING),
                    fieldWithPath("password").description("Password of the user requested").type(STRING),
                    fieldWithPath("_links.self.href").description("Link to access the requested user").type(STRING),
                    fieldWithPath("_links.user.href").description("Link to access the requested user").type(STRING)
                )
            ))
    }

    @Test
    fun `Update users request`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/users/1")
            .content(objectMapper.writeValueAsString(
                UserDTO(
                    name = "Test user to update",
                    password = "TestUserUpdate2018"
                )
            ))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
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
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/users/52")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isNoContent)
            .andDo(document("user_delete"
            ))
    }
}

internal data class UserDTO(
    val name: String,
    val password: String
)
