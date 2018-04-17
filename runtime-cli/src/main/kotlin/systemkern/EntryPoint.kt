package systemkern

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import java.math.BigDecimal
import java.nio.file.Paths
import javax.annotation.PostConstruct


@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackages = ["systemkern"])
class EntryPoint

/**
 * Attention: Because of relative paths during app startup you might have to
 * set a working directory in your IDE:
 * For executing this class use: main/target/classes as the working directory for your execution profile
 */
fun main(args: Array<String>) {
    SpringApplication.run(EntryPoint::class.java, *args)
}
