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
        def enumValueDefs = enumDescription.values.collect{generateEnumValueDef(enumDescription.name, it)}.join(",\n")

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
    @Nonnull
    @Override
    public String getCode() {
        return code;
    }
    
    /**
     * Выполнить действие над элементом перечисления, в зависимости от Посетителя.
     *
     * @param visitor посетитель
     * @param <V>     действие, характерное для даного Посетителя
     * @return действие, характерное для даного Посетителя
     */
    public abstract <V> V visit(${enumDescription.name}Visitor<V> visitor);
    
    @Nonnull
    @Override
    public String toString() {
        return "${enumDescription.name}{" + 
               "code=" + code +
               '}';
    }
    
    @JsonCreator
    public static ${enumDescription.name} byCode(@Nonnull String code) {
        return Enums.byCode(${enumDescription.name}.class, code);
    }
}
"""
        Utils.toFile("out/" + enumDescription.name, definition)

        new EnumVisitorGenerator().generate(enumDescription)
    }

    static String generateEnumValueDef(String enumName, EnumValue enumValue) {
        """
    /**
     * ${enumValue.javadoc}
     */
    ${enumValue.name.toUpperCase()}("${enumValue.repr}") {
        @Override
        public <V> V visit(${enumName}Visitor<V> visitor) {
            return visitor.${Utils.toCamelCase(enumValue.name.toLowerCase())}();
        }
    }"""
    }
}
