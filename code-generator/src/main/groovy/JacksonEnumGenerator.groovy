import com.fasterxml.jackson.databind.ObjectMapper

import java.time.ZonedDateTime

/**
 * Created by knovokresch on 16.02.2018.
 */
class JacksonEnumGenerator {

    static void main(String[] args) {
        def enumList = new ObjectMapper().readValue(new File('enums.json').text, EnumDescriptionList)
        def generator = new JacksonEnumGenerator()
        enumList.enums.each {generator.generate(it)}
    }

    void generate(EnumDescription enumDescription) {
        def now = ZonedDateTime.now()
        def enumValueDefs = enumDescription.values.collect{generateEnumValueDef(it)}.join(",\n")

        def definition = """

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import ru.yandex.money.common.util.Enums;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * ${enumDescription.javadoc} 
 * 
 * @author Konstantin Novokreshchenov (knovokresch@yamoney.ru)
 * @since ${now.dayOfMonth}.${now.monthValue < 10 ? "0${now.monthValue}" : now.monthValue}.${now.year}
 */
public enum ${enumDescription.name} implements Enums.StringRepr {
    ${enumValueDefs}

    ;
    
    static {
        Enums.checkUniqueCodes(${enumDescription.name}.class);
    }
    
    private final String code;
    
    ${enumDescription.name}(@Nonnull String code) {
        this.code = requireNonNull(code);
    }
    
    @JsonValue
    @Override
    @Nonnull
    public String getCode() {
        return code;
    }
    
    @JsonCreator
    public ${enumDescription.name} byCode(@Nonnull String code) {
        return Enums.byCode(${enumDescription.name}.class, code);
    }
}
"""
        toFile(enumDescription.name, definition)
    }

    static String generateEnumValueDef(EnumValue enumValue) {
        """
    /**
     * ${enumValue.javadoc}
     */
    ${enumValue.name.toUpperCase()}("${enumValue.repr}")"""
    }

    static void toFile(String enumName, String enumDef) {
        def file = new File("out/${enumName}.java.gen", )
        file.withWriter('UTF-8') { writer ->
            writer.write(enumDef)
        }
    }
}
