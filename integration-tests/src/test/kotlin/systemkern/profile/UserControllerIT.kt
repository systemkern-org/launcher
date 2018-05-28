package systemkern.profile

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import systemkern.CliEntryPoint
import systemkern.IntegrationTest

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CliEntryPoint::class])
internal class UserControllerIT : IntegrationTest() {

    @Autowired
    private lateinit var testDataCreator: UserProfileTestDataCreator

    @Before
    fun setup() {
        testDataCreator.persistTestData()
    }

    @Test
    fun `Can Create a User`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/users")
            .content(objectMapper.writeValueAsString(
                TestUser(
                    name = "Test User",
                    password = "*TestUser2018*"
                )
            ))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isCreated)
            .andDo(document("user_create",
                requestFields(
                    fieldWithPath("name").description("Name of user").type(STRING),
                    fieldWithPath("password").description("Password of user to be created").type(STRING)
                ),
                responseFields(
                    fieldWithPath("name").description("Name of the user").type(STRING),
                    fieldWithPath("_links.self.href").description("Link to access the created user").type(STRING),
                    fieldWithPath("_links.user.href").description("Link to access the created user").type(STRING)
                )
            ))
    }

    @Test
    fun `Can Read single User`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/users/${UserProfileTestDataCreator.userId}")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_read",
                responseFields(
                    fieldWithPath("name").description("Name of the user requested").type(STRING),
                    fieldWithPath("_links.self.href").description("Link to access the requested user").type(STRING),
                    fieldWithPath("_links.user.href").description("Link to access the requested user").type(STRING)
                )
            ))
    }

    @Test
    fun `Can update user`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/users/${UserProfileTestDataCreator.userId}")
            .content(objectMapper.writeValueAsString(
                TestUser(
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
                ),
                responseFields(
                    fieldWithPath("name").description("Name of the user requested").type(STRING),
                    fieldWithPath("_links.self.href").description("Link to access the requested user").type(STRING),
                    fieldWithPath("_links.user.href").description("Link to access the requested user").type(STRING)
                )
            ))
    }

    @Test
    fun `Can delete Users`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/users/${UserProfileTestDataCreator.userId}")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isNoContent)
            .andDo(document("user_delete"))
    }
}

private data class TestUser(
    val name: String,
    val password: String
)
