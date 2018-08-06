package systemkern

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan(basePackages = ["systemkern"])
@EnableJpaRepositories(basePackages = ["systemkern"])
@EntityScan(basePackages = ["systemkern"])
class CliEntryPoint

/**
 * Attention: Because of relative paths during app startup you might have to
 * set a working directory in your IDE:
 * For executing this class use: main/target/classes as the working directory for your execution profile
 */
fun main(args: Array<String>) {
    SpringApplication.run(CliEntryPoint::class.java, *args)
}
