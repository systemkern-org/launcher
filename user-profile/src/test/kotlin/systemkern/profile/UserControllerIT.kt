package systemkern.profile

import org.json.JSONObject
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

//@Ignore("Swagger Swagger Shaggy")
@EnableAutoConfiguration
internal class UserControllerIT : IntegrationTest() {
    private val nameExample = "AndresAusecha"
    private val usernameExample = nameExample + "18"
    private val emailExample = "$nameExample@gmail.com"
    private val passwordExample = usernameExample.plus("*")
    private val httpHeaders = HttpHeaders()
    private val restUrl = "/user-profiles"
    private var token: String = ""
    private var urlToVerifyUserProfile = ""
    private val emailChangeURL = "/email-change"
    private var emailChangeToken = ""

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
        createUser(TestUser(
            name = nameExample,
            password = passwordExample,
            username = usernameExample,
            email = emailExample
        ))
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
            .andReturn().response.contentAsString.let { this.urlToVerifyUserProfile = JSONObject(it).get("url").toString() }
        verifyEmail()
    }

    private fun verifyEmail(){
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(this.urlToVerifyUserProfile)
            .headers(httpHeaders)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString.let { this.token = "Bearer " + JSONObject(it).get("token").toString() }
    }

    @Test
    fun `Can change User email`() {
        createUser(TestUser(
            name = nameExample,
            password = passwordExample,
            username = usernameExample,
            email = emailExample))

       this.mockMvc.perform(post("$emailChangeURL")
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

internal data class EmailChangeRequest(val newEmailAddress: String, val userProfileId: UUID)
