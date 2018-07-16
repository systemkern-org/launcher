package systemkern

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@ActiveProfiles("integration-test")
@ComponentScan(basePackages = ["systemkern"])
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CliEntryPoint::class])
@EnableAutoConfiguration
class SwaggerIT {

    val restDocumentation = JUnitRestDocumentation()
    @Autowired
    lateinit var objectMapper: ObjectMapper
    @Autowired
    lateinit var context: WebApplicationContext
    lateinit var mockMvc: MockMvc
    @get:Rule
    val rules = RuleChain
        .outerRule(restDocumentation)

    @Before
    fun setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation))
            //.apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    @Test fun `Can open Swagger JSON`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/v2/api-docs")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    @Test fun `Can open Swagger UI`() {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/swagger-ui.html#/")
            .accept(ALL))
            .andExpect(status().isOk)
    }

}