package systemkern.profile

import org.json.JSONObject
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import systemkern.CliEntryPoint
import systemkern.IntegrationTest
import java.util.*
import kotlin.collections.HashMap

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CliEntryPoint::class])

internal class UserControllerIT : IntegrationTest() {
    private val httpHeaders = HttpHeaders()
    val headers: HashMap<String,String> = HashMap()
    init {
        headers["username"] = "AndresAusecha18"
        headers["password"] = "AndresAusecha18*"
        httpHeaders.setAll(headers)
    }
    private val restUrl = "/user-profiles"
    private val restLogin = "/login"
    private var token: String = ""
    private val entityRequestFields = listOf(
        fieldWithPath("name").description("Name of the user").type(STRING),
        fieldWithPath("username").description("Username of the user").type(STRING),
        fieldWithPath("password").description("Password of user to be created").type(STRING)
    )
    private val entityResponseFields = listOf(
        fieldWithPath("id").description("The Id of the user entity").type(STRING),
        fieldWithPath("name").description("Name of the user").type(STRING),
        fieldWithPath("username").description("Username to log in").type(STRING),
        fieldWithPath("_links.self.href").description("Link to access the created user").type(STRING),
        fieldWithPath("_links.userProfile.href").description("Link to access the created user").type(STRING)
    )

    @Autowired
    private lateinit var testDataCreator: UserProfileTestDataCreator
    private lateinit var userId: UUID

    @Before
    fun setup() {
        testDataCreator.persistTestData()
        this.userId = testDataCreator.userId
    }

    private fun `create user function`(user: TestUser)
    {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(restUrl)
            .content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isCreated)
            .andDo(document("user_create",
                responseFields(entityResponseFields)
            ))
    }
    fun `login function`(username: String,password: String)
    {
        headers["username"] = username
        headers["password"] = password
        httpHeaders.setAll(headers)

        this.mockMvc.perform(RestDocumentationRequestBuilders.post(restLogin)
        .headers(httpHeaders)
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON))
        .andExpect(status().isOk)
        .andDo(document("user_login"))
        .andReturn().response.contentAsString.let { token = "Bearer " + JSONObject(it).get("token").toString() }
    }


    @Test
    fun `Can create a User`() {
        `create user function`(TestUser(
            username = "AndresAusecha18",
            name = "Andres Ausecha",
            password = "AndresAusecha18*"
        ))
    }

    @Test
    fun `Can login User`() {
        val username = "RainerKern01"
        val password = "RainerKern01*"
        `create user function`(TestUser(
            username = username,
            name = "Rainer Kern",
            password = password
        ))
        `login function`(username,password)
    }

    @Test
    fun `Can read User`() {
        val username = "AndresAusecha19"
        val password = "AndresAusecha19*"
        `create user function`(TestUser(
            username = username,
            name = "Andres Ausecha",
            password = password
        ))
        `login function`(username, password)
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("$restUrl/$userId")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_read",
                responseFields(entityResponseFields)
            ))
    }

    @Test
    fun `Can update User`() {
        val username = "AndresAusecha20"
        val password = "AndresAusecha20*"
        `create user function`(TestUser(
            username = username,
            name = "Andres Ausecha",
            password = password
        ))
       `login function`(username, password)
        this.mockMvc.perform(RestDocumentationRequestBuilders.put("$restUrl/$userId")
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(
                objectMapper.writeValueAsString(
                    TestUser(
                        username = "TestUserToUpdate",
                        name = "Test user to update",
                        password = "TestUserToUpdate*"
                    )
                ))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_update",
                responseFields(entityResponseFields)
            ))
    }

    @Test
    fun `Cannot delete User`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("$restUrl/$userId")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isForbidden)
            .andDo(document("user_delete"))
    }
}

private data class TestUser(
    val username: String,
    val name: String,
    val password: String
)
