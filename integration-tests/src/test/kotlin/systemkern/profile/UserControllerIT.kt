package systemkern.profile

import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
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
    private val nameExample = "AndresAusecha"
    private val nameExample1 = "RainerKern"
    private val usernameExample = nameExample + "18"
    private val passwordExample = usernameExample.plus("*")
    private val usernameExample1 = nameExample + "19"
    private val passwordExample1 = usernameExample.plus("*")
    private val usernameExample2 = nameExample + "20"
    private val passwordExample2 = usernameExample.plus("*")
    private val usernameExample3 = nameExample1.plus("01")
    private val passwordExample3 = usernameExample.plus("*")
    private val httpHeaders = HttpHeaders()
    private val restUrl = "/user-profiles"
    private val emailVerify = "/user-profiles/verify-email"
    private val restLogin = "/login"
    private var token: String = ""
    val headers: HashMap<String, String> = HashMap()
    private var usernameDesc = "Username to log in"
    private var username = "username"
    
    private val entityResponseFields = listOf(
        fieldWithPath("id").description("The Id of the user entity").type(STRING),
        fieldWithPath("name").description("Name of the user").type(STRING),
        fieldWithPath(username).description(usernameDesc).type(STRING),
        fieldWithPath("_links.self.href").description("Link to access the created user").type(STRING),
        fieldWithPath("_links.userProfile.href").description("Link to access the created user").type(
            STRING)
    )
    private val loginResponseFields = responseFields(listOf(
    fieldWithPath("token").description("Token to authenticate the next requests")
    .type(STRING),
    fieldWithPath(username).description(usernameDesc).type(STRING),
    fieldWithPath("userId").description("Password of user to be created").type(STRING),
    fieldWithPath("validUntil").description("Date and Time until session will expire")
    .type(STRING)
    ))
    @Autowired
    private lateinit var testDataCreator: UserProfileTestDataCreator
    private lateinit var userId: UUID

    @Before
    fun setup() {
        testDataCreator.persistTestData()
        this.userId = testDataCreator.userId
    }

    private fun `create user function`(user: TestUser) {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(restUrl)
            .content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isCreated)
            .andDo(document("user_create",
                responseFields(entityResponseFields)
            ))
    }

    private fun `login function`(username: String, password: String) {
        headers[this.username] = username
        headers["password"] = password
        httpHeaders.setAll(headers)
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(restLogin)
            .headers(httpHeaders)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_login",loginResponseFields))
            .andReturn().response.contentAsString.let { token = "Bearer " + JSONObject(it).get("token").toString() }
    }

    @Test
    fun `Can create a User`() {
        `create user function`(TestUser(
            username = usernameExample,
            name = nameExample,
            password = passwordExample
        ))
    }

    @Test
    fun `Can login User`() {
        val username = usernameExample3
        val password = passwordExample3
        `create user function`(TestUser(
            username = username,
            name = nameExample1,
            password = password
        ))
        `login function`(username, password)
    }

    @Test
    fun `Can read User`() {
        val username = usernameExample1
        val password = passwordExample1
        `create user function`(TestUser(
            username = username,
            name = nameExample,
            password = password
        ))
        `login function`(username, password)
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("$restUrl/$userId")
            .header(AUTHORIZATION, token)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_read",
                responseFields(entityResponseFields)
            ))
    }
    @Test
    fun `Can verify email User`() {
     /*   val username = usernameExample1
        val password = passwordExample1
        `create user function`(TestUser(
            username = username,
            name = nameExample,
            password = password
        ))
        `login function`(username, password)*/
        this.mockMvc.perform(RestDocumentationRequestBuilders.put("$emailVerify")
            //.header(AUTHORIZATION, token)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_email_verify",
                responseFields(entityResponseFields)
            ))
    }

    @Test
    fun `Can update User`() {
        val username = usernameExample2
        val password = passwordExample2
        `create user function`(TestUser(
            username = username,
            name = nameExample,
            password = password
        ))
        `login function`(username, password)
        this.mockMvc.perform(RestDocumentationRequestBuilders.put("$restUrl/$userId")
            .header(AUTHORIZATION, token)
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
