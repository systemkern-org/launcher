package systemkern.profile

import org.json.JSONObject
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*
/*

@EnableAutoConfiguration
internal class AuthenticationIT : IntegrationTest() {
    private val nameExample = "AndresAusecha"
    private val nameExample1 = "RainerKern"
    private val usernameExample = nameExample + "18"
    private val httpHeaders = HttpHeaders()
    private val restUrl = "/user-profiles"
    private val restLogin = "/login"
    private var token: String = ""
    val headers: HashMap<String, String> = HashMap()
    private var usernameDesc = "Username to log in"
    private var username = "username"

    private val entityResponseFields = listOf(
        PayloadDocumentation.fieldWithPath("id").description("The Id of the user entity").type(JsonFieldType.STRING),
        PayloadDocumentation.fieldWithPath("name").description("Name of the user").type(JsonFieldType.STRING),
        PayloadDocumentation.fieldWithPath(username).description(usernameDesc).type(JsonFieldType.STRING),
        PayloadDocumentation.fieldWithPath("_links.self.href").description("Link to access the created user").type(JsonFieldType.STRING),
        PayloadDocumentation.fieldWithPath("_links.userProfile.href").description("Link to access the created user").type(
            JsonFieldType.STRING)
    )
    private val loginResponseFields = PayloadDocumentation.responseFields(listOf(
        PayloadDocumentation.fieldWithPath("token").description("Token to authenticate the next requests").type(JsonFieldType.STRING),
        PayloadDocumentation.fieldWithPath(username).description(usernameDesc).type(JsonFieldType.STRING),
        PayloadDocumentation.fieldWithPath("userId").description("Password of user to be created").type(JsonFieldType.STRING),
        PayloadDocumentation.fieldWithPath("validUntil").description("Date and Time until session will expire").type(JsonFieldType.STRING)))

    @Autowired
    private lateinit var testDataCreator: UserProfileTestDataCreator
    private lateinit var userId: UUID

    @Before
    fun setup() {
        testDataCreator.persistTestData()
        this.userId = testDataCreator.userId
    }

    private fun `create user function`(user: TestAuthUser) {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(restUrl)
            .content(objectMapper.writeValueAsString(user))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andDo(MockMvcRestDocumentation.document("user_create",
                PayloadDocumentation.responseFields(entityResponseFields)
            ))
    }

    private fun `login function`(username: String, password: String) {
        headers[this.username] = username
        headers["password"] = password
        httpHeaders.setAll(headers)
        this.mockMvc.perform(RestDocumentationRequestBuilders.post(restLogin)
            .headers(httpHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcRestDocumentation.document("user_login", loginResponseFields))
            .andReturn().response.contentAsString.let { token = "Bearer " + JSONObject(it).get("token").toString() }
    }
}

private data class TestAuthUser(
    val username: String,
    val name: String,
    val password: String
)
*/