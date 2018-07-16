package systemkern

import io.swagger.annotations.Api
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport




@Configuration
@EnableSwagger2
class SwaggerConfig : WebMvcConfigurationSupport() {

    @Bean fun api(): Docket {
        return Docket(DocumentationType.SPRING_WEB)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
    }

    @Bean fun apiInfo(): ApiInfo {
        val builder = ApiInfoBuilder()
        builder.title("Swagger Test Api ... le documentation").version("1.0").license("(C) Copyright Test")
            .description("The API provides a platform to query build test swagger api")

        return builder.build()
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
        registry!!.addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/")

        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
    }
}

@Api
@Controller
@RequestMapping("/asdasdasd")
class SwaggerController {

    @GetMapping
    fun swaggerUi(): String {
        return "redirect:/swagger-ui.html";
    }
}
