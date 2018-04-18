package systemkern

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan


@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackages = ["systemkern"])
class CliEntryPoint

/**
 * Attention: Because of relative paths during app startup you might have to
 * set a working directory in your IDE:
 * For executing this class use: main/target/classes as the working directory for your execution profile
 */
fun main(args: Array<String>) {
    SpringApplication.run(CliEntryPoint::class.java, *args)
}
