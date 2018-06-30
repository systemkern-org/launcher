package systemkern

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["systemkern"]) //add custom packages for scanning here
class GaeStandardEntryPoint : SpringBootServletInitializer()

fun main(args: Array<String>) {
	SpringApplication.run(GaeStandardEntryPoint::class.java, *args)
}
