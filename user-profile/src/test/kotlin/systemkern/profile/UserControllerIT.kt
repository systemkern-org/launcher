package systemkern.profile

import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import systemkern.IntegrationTest
import java.util.*

@EnableAutoConfiguration
internal class UserControllerIT : IntegrationTest() {

    private val restUrl = "/users"

    private val entityRequestFields = listOf(
        fieldWithPath("name").description("Name of the user").type(STRING),
        fieldWithPath("password").description("Password of user to be created").type(STRING)
    )
    private val entityResponseFields = listOf(
        fieldWithPath("id").description("The Id of the user entity").type(STRING),
        fieldWithPath("name").description("Name of the user").type(STRING),
        fieldWithPath("_links.self.href").description("Link to access the created user").type(STRING),
        fieldWithPath("_links.user.href").description("Link to access the created user").type(STRING)
    )

    @Autowired
    private lateinit var testDataCreator: UserProfileTestDataCreator
    private lateinit var userId: UUID

    @Before
    fun setup() {
        testDataCreator.persistTestData()
        this.userId = testDataCreator.userId
    }

    @Test
    fun `Can create a User`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(restUrl)
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
                requestFields(entityRequestFields),
                responseFields(entityResponseFields)
            ))
    }

    @Test
    fun `Can read User`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("$restUrl/$userId")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_read",
                responseFields(entityResponseFields)
            ))
    }

    @Test
    fun `Can update User`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.put("$restUrl/$userId")
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
                requestFields(entityRequestFields),
                responseFields(entityResponseFields)
            ))
    }

    @Test
    fun `Can delete User`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("$restUrl/$userId")
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
