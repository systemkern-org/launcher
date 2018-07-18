package systemkern.profile

import org.json.JSONObject
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import kotlin.collections.HashMap

@Ignore("Swagger Swagger Shaggy")
@EnableAutoConfiguration
internal class UserControllerIT : IntegrationTest() {
    private val nameExample = "AndresAusecha"
    private val nameExample1 = "RainerKern"
    private val usernameExample = nameExample + "18"
    private val emailExample = nameExample + "@gmail.com"
    private val passwordExample = usernameExample.plus("*")
    private val usernameExample1 = nameExample + "19"
    private val passwordExample1 = usernameExample.plus("*")
    private val usernameExample2 = nameExample + "20"
    private val passwordExample2 = usernameExample.plus("*")
    private val usernameExample3 = nameExample1.plus("01")
    private val passwordExample3 = usernameExample.plus("*")
    private val httpHeaders = HttpHeaders()
    private val restUrl = "/user-profiles"
    private val restLogin = "/login"
    private val passwordResetUrl = "/password-reset"
    private var token: String = ""
    private val headers: HashMap<String, String> = HashMap()
    private var usernameDesc = "Username to log in"
    private var username = "username"
    private var urlToVerifyUserProfile = ""
    private var passwordResetEntityId: String = ""
    private var verifyEmailAccessToken: String = ""

    private val entityResponseFields = listOf(
        fieldWithPath("id").description("The Id of the user entity").type(STRING),
        fieldWithPath("name").description("Name of the user").type(STRING),
        fieldWithPath(username).description(usernameDesc).type(STRING),
        fieldWithPath("email").description("User's email").type(STRING),
        fieldWithPath("_links.self.href").description("Link to access the created user").type(STRING),
        fieldWithPath("_links.userProfile.href").description("Link to access the created user").type(
            STRING),
        fieldWithPath("_links.emailVerificationList.href").description(
            "Link to access verification tokens generated").type(STRING)
    )
    private val loginResponseFields = responseFields(listOf(
    fieldWithPath("token").description("To authenticate the next requests")
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

    private fun createUser(user: TestUser,verify: Boolean) {
        this.mockMvc.perform(post(restUrl)
            .content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_create",
                responseFields(listOf(
                    fieldWithPath("url").description("Url to verify user email").type(STRING))
                )))
            .andReturn().response.contentAsString.let { urlToVerifyUserProfile = "url" +
                    JSONObject(it).get("url").toString() }
        if(verify) {
            verifyEmail(user.username,user.password)
        }
    }

    private fun verifyEmail(username: String, password: String) {
        createHeadersObject(username, password)
        this.mockMvc.perform(post(urlToVerifyUserProfile)
            .headers(httpHeaders)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_verify", loginResponseFields))
            .andReturn().response.contentAsString.let { verifyEmailAccessToken = JSONObject(it).get("token").toString() }
    }

    private fun loginFunction(username: String, password: String) {
        createHeadersObject(username, password)
        this.mockMvc.perform(post(restLogin)
            .headers(httpHeaders)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_login", loginResponseFields))
            .andReturn().response.contentAsString.let { token = "Bearer " + JSONObject(it).get("token").toString() }
    }

    private fun resetPasswordFunction(username: String, password: String) {
        createHeadersObject(username, password)
        headers[AUTHORIZATION] = verifyEmailAccessToken
        buildResetPasswordFunction(passwordResetUrl)
            .andDo(document("resetPassword", responseFields(listOf(
                fieldWithPath("id").description("The Id of the user entity").type(STRING),
                fieldWithPath("creationDate").description("Date which was created the request").type(STRING),
                fieldWithPath("validUntil").description("Date until token is valid").type(STRING),
                fieldWithPath("completionDate").description("Date in which request is confirmed").type(STRING)))))
            .andReturn().response.contentAsString.let { passwordResetEntityId = JSONObject(it).get("id").toString() }
    }

    private fun resetPasswordFunctionWithId() {
        buildResetPasswordFunction("$passwordResetUrl/$passwordResetEntityId")
    }

    private fun buildResetPasswordFunction(url: String): ResultActions =
        this.mockMvc.perform(post(url
        )
            .headers(httpHeaders)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(NewPasswordResetBody(password = "NewPassword*")))
            .accept(APPLICATION_JSON)).andExpect(status().isOk)

    private fun createHeadersObject(username: String, password: String) {
        headers[this.username] = username
        headers["password"] = password
        httpHeaders.setAll(headers)
    }

    @Test
    fun `Can create a User`() {
        createUser(TestUser(
            username = usernameExample,
            name = nameExample,
            password = passwordExample,
            email = emailExample
        ),true)
    }

    @Test
    fun `Can login User`() {
        val username = usernameExample3
        val password = passwordExample3
        createUser(TestUser(
            username = username,
            name = nameExample1,
            password = password,
            email = emailExample
        ),false)
    }

    @Test
    fun `Can reset password`() {
        val username = usernameExample3
        val password = passwordExample3
        createUser(TestUser(
            username = username,
            name = nameExample1,
            password = password,
            email = emailExample
        ),true)
        verifyEmail(username, password)
        resetPasswordFunction(username, password)
        resetPasswordFunctionWithId()
    }

    @Test
    fun `Can read User`() {
        val username = usernameExample1
        val password = passwordExample1
        createUser(TestUser(
            username = username,
            name = nameExample,
            password = password,
            email = emailExample
        ),true)
        loginFunction(username, password)
        this.mockMvc.perform(get("$restUrl/$userId")
            .header(AUTHORIZATION, token)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_read",
                responseFields(entityResponseFields)
            ))
    }

    @Test
    fun `Can update User`() {
        val username = usernameExample2
        val password = passwordExample2
        createUser(TestUser(
            username = username,
            name = nameExample,
            password = password,
            email = emailExample
        ),true)
        loginFunction(username, password)
        this.mockMvc.perform(put("$restUrl/$userId")
            .header(AUTHORIZATION, token)
            .content(
                objectMapper.writeValueAsString(
                    TestUser(
                        username = "TestUserToUpdate",
                        name = "Test user to update",
                        password = "TestUserToUpdate*",
                        email = emailExample
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
        this.mockMvc.perform(delete("$restUrl/$userId")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isForbidden)
            .andDo(document("user_delete"))
    }
}

private data class TestUser(
    val username: String,
    val name: String,
    val password: String,
    val email: String
)