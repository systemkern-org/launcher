package systemkern

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.stereotype.Component
import org.zalando.jackson.datatype.money.MoneyModule

/**
 * This class is deliberatly left public so it is availible to all RestControllers in all modules on the classpath
 */
@Component
class JsonObjectMapper : ObjectMapper() {

    init {
        this.registerModule(KotlinModule())
        // The java time module maps time as an array instead of as an object
        this.registerModule(JavaTimeModule())
        this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        //Zalando money offers easier mapping for Money datatypes (CCY, Amount) than the standard mapping
        this.registerModule(MoneyModule())
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        this.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        this.configure(SerializationFeature.INDENT_OUTPUT, true)
    }

}
