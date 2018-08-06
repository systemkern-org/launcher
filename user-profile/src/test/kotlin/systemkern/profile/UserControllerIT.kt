package systemkern.profile

import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import kotlin.collections.HashMap

@EnableAutoConfiguration
internal class UserControllerIT : IntegrationTest() {
    private val nameExample = "AndresAusecha"
    private val usernameExample = nameExample + "18"
    private val emailExample = "$nameExample@gmail.com"
    private val passwordExample = usernameExample.plus("*")
    private val usernameExample1 = nameExample + "19"
    private val passwordExample1 = usernameExample.plus("*")
    private val usernameExample2 = nameExample + "20"
    private val passwordExample2 = usernameExample.plus("*")
    private val usernameExample3 = nameExample + "21"
    private val passwordExample3 = usernameExample.plus("*")
    private val usernameExample4 = nameExample + "22"
    private val passwordExample4 = usernameExample.plus("*")
    private val httpHeaders = HttpHeaders()
    private val restUrl = "/user-profiles"
    private val restLogin = "/auth"
    private var token: String = ""
    private val headers: HashMap<String, String> = HashMap()
    private var usernameDesc = "Username to log in"
    private var username = "username"
    private var urlToVerifyUserProfile = ""
    private val emailChangeURL = "/email-change"
    private var emailChangeToken = ""
    private var passwordResetEntityId: String = ""
    private val passwordResetUrl = "/password-reset"
  
    private val entityResponseFields = listOf(
        fieldWithPath("name").description("Name of the user").type(STRING),
        fieldWithPath(username).description(usernameDesc).type(STRING),
        fieldWithPath("email").description("User's email").type(STRING),
        fieldWithPath("_links.self.href").description("User's email").type(STRING),
        fieldWithPath("_links.userProfile.href").description("Link to access user profile").type(STRING),
        fieldWithPath("_links.passwordResetList.href")
            .description("Link to access password reset children").type(STRING),
        fieldWithPath("_links.emailVerificationList.href")
            .description("Link to access Email verification children").type(STRING),
          fieldWithPath("_links.emailChangeList.href")
            .description("Link to access Email Change children").type(STRING)
    )

    private val loginResponseFields = responseFields(listOf(
        fieldWithPath("token").description("Token to authenticate the next requests").type(STRING),
        fieldWithPath(username).description(usernameDesc).type(STRING),
        fieldWithPath("userId").description("Password of user to be created").type(STRING),
        fieldWithPath("validUntil").description("Date and Time until session will expire").type(STRING)))

    @Autowired
    private lateinit var testDataCreator: UserProfileTestDataCreator
    private lateinit var userId: UUID

    @Before
    fun setup() {
        testDataCreator.persistTestData()
        this.userId = testDataCreator.userId
    }

    private fun createUser(user: TestUser) {
        this.mockMvc.perform(post(restUrl)
            .content(objectMapper.writeValueAsString(user))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_create",
                responseFields(listOf(
                    fieldWithPath("url").description("Url to verify user email").type(STRING))
                )))
            .andReturn().response.contentAsString.let {
            this.urlToVerifyUserProfile = JSONObject(it).get("url").toString()
        }
        verifyEmail()
    }

    private fun verifyEmail(){
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(this.urlToVerifyUserProfile)
            .headers(httpHeaders)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString.let {
                val jsonObjectRes = JSONObject(it)
                this.token = "Bearer " + jsonObjectRes.get("token").toString()
                this.userId = UUID.fromString(jsonObjectRes.get("userId").toString())
            }
    }

    private fun loginFunction(username: String, password: String) {
        createHeadersObject(username,password)
        headers[this.username] = username
        headers["password"] = password
        httpHeaders.setAll(headers)
        this.mockMvc.perform(post(restLogin)
            .headers(httpHeaders)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_login",loginResponseFields))
            .andReturn().response.contentAsString.let {
                this.token = "Bearer " + JSONObject(it).get("token").toString()
        }
    }

    private fun createHeadersObject(username: String, password: String){
        headers[this.username] = username
        headers["password"] = password
        httpHeaders.setAll(headers)
    }

    private fun resetPasswordFunction(username: String, password: String) {
        createHeadersObject(username, password)
        headers[AUTHORIZATION] = token
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
        this.mockMvc.perform(post(url)
            .headers(httpHeaders)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(NewPasswordResetBody(password = "NewPassword*")))
            .accept(APPLICATION_JSON)).andExpect(status().isOk)

    @Test
    fun `Can create a User`() {
        createUser(TestUser(
            name = nameExample,
            password = passwordExample,
            username = usernameExample,
            email = emailExample
        ))
    }

    @Test
    fun `Can reset password`() {
        val username = usernameExample3
        val password = passwordExample3
        createUser(TestUser(
            username = username,
            name = nameExample,
            password = password,
            email = emailExample
        ))
        resetPasswordFunction(username, password)
        resetPasswordFunctionWithId()
    }

    @Test
    fun `Can login User`() {
        val username = usernameExample4
        val password = passwordExample4
        createUser(TestUser(
            username = username,
            name = nameExample,
            password = password,
            email = emailExample
        ))
        loginFunction(username, password)
    }

    @Test
    fun `Can read User`() {
        createUser(TestUser(
            username = usernameExample1,
            name = nameExample,
            password = passwordExample1,
            email = emailExample
        ))
        this.mockMvc.perform(get("$restUrl/$userId")
            .header(AUTHORIZATION, this.token)
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
        ))
        this.mockMvc.perform(put("$restUrl/$userId")
            .header(AUTHORIZATION, this.token)
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

    @Test
    fun `Can change User email`() {
        createUser(TestUser(
            name = nameExample,
            password = passwordExample,
            username = usernameExample,
            email = emailExample
        ))

       this.mockMvc.perform(post(emailChangeURL)
            .content(objectMapper.writeValueAsString(
                EmailChangeRequest(
                    newEmailAddress = "testChangeEmail@gmail.com",
                    userProfileId = userId
                )))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_email_change",
                responseFields(listOf(
                fieldWithPath("emailChangeReqId").description("Token of email change request").type(STRING),
                fieldWithPath("validUntil").description("Time until token is valid").type(STRING)))))
            .andReturn().response.contentAsString.let {
                emailChangeToken = JSONObject(it).get("emailChangeReqId").toString()
            }

        this.mockMvc.perform(post("$emailChangeURL/$emailChangeToken")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andDo(document("user_email_change_confirmation",
            responseFields(listOf(
            fieldWithPath("token").description("Token to authenticate future request").type(STRING),
            fieldWithPath("username").description("User name of user profile").type(STRING),
            fieldWithPath("userId").description("Id of user profile entity").type(STRING),
            fieldWithPath("validUntil").description("Time until token is valid").type(STRING)))))
    }
}

private data class TestUser(
    val username: String,
    val name: String,
    val password: String,
    val email: String
)

internal data class EmailChangeRequest(
    val newEmailAddress: String,
    val userProfileId: UUID
)
