import java.time.LocalDate

/**
 * @author Konstantin Novokreshchenov (knovokresch@yamoney.ru)
 * @since 19.06.2017
 */
class JacksonClassGenerator {

    static ClassName = "BookEntity"

    static ClassDescription = [
            'id': 'Integer',
            'title': 'String',
            'author': 'String',
            'totalPagesCount': 'Integer',
            'coverImage': 'String'
    ]

    static void main(String[] args) {

        new JacksonClassGenerator(
                ClassName,
                ClassDescription
        ).generate()
    }

    private final String className
    private final Map<String, String> classDescription
    private final List<JavaField> javaFields

    JacksonClassGenerator(String className, Map<String, String> classDescription) {
        this.className = className
        this.classDescription = classDescription
        this.javaFields = classDescription.entrySet().collect({ new JavaField(it.value, it.key, it.key) })
    }

    private void generate() {
        def now = LocalDate.now()
        String classDef = """
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ru.yandex.money.common.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;


/**
 * @author Konstantin Novokreshchenov (knovokresch@yamoney.ru)
 * @since ${now.dayOfMonth}.${now.monthValue < 10 ? "0${now.monthValue}" : now.monthValue}.${now.year}
 */
@ApiModel("<h5></h5>")
public class $className implements JsonObject {

${generateFields()}

${generateConstructor()}

${generateGetters()}

${generateEquals()}

${generateHashCode()}

${generateToString()}

${generateEmptyStaticBuilder()}

${generateFilledStaticBuilder()}

${generateBuilder()}
}"""
        def file = new File("out/${className}.java.gen")
        file.text = classDef
    }

    private String generateFields() {
        String.join("\n\n", classDescription.collect {
            """@ApiModelProperty(
            value = "<h6></h6>",
            required = true,
            example = ""
    )
private final ${it.value} ${it.key};"""
        })
    }

    private String generateConstructor() {
        """@JsonCreator
public $className(${generateConstructorParameters()}) {
${generateFieldAssignments()}
}"""
    }

    private String generateConstructorParameters() {
        String.join(",\n", classDescription.collect { generateConstructorParameter(it.key, it.value) })
    }

    private String generateConstructorParameter(String parameterName, String type) {
        """@JsonProperty("$parameterName") @Nonnull $type $parameterName"""
    }

    private String generateFieldAssignments() {
        String.join("\n", classDescription.collect { generateFieldAssignment(it.key) })
    }

    private String generateFieldAssignment(String fieldName) {
        """this.$fieldName = $fieldName;"""
    }

    private String generateGetters() {
        String.join('\n\n', classDescription.collect { generateGetter(it.key, it.value) })
    }

    private String generateGetter(String name, String type) {
        """@Nonnull
@JsonProperty("$name")
public $type get${Utils.fromUpperCase(name)}() {
return $name;
}"""
    }

    private String generateEquals() {
        """@Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        $className other = ($className) obj;
        return ${generateObjectEquals()};
    }"""
    }

    private String generateObjectEquals() {
        String.join(
                " && \n",
                classDescription.collect { """Objects.equals(${it.key}, other.${it.key})""" }
        )
    }

    private String generateToString() {
        def fieldStrings = Utils.transform(
                classDescription.collect {it.key },
                { name -> """\"$name=\" + $name +""" },
                { name -> """\", $name=\" + $name +""" },
        )

        def fields = String.join("\n", fieldStrings)

        """@Override
    public String toString() {
        return "$className" +
                ${fields}
                '}';
    }"""
    }

    private String generateHashCode() {
        def fields = String.join(", ", classDescription.collect { it.key })
        """@Override
    public int hashCode() {
        return Objects.hash(${fields});
    }"""
    }

    String generateEmptyStaticBuilder() {
        """/**
 * Создает билдер для построения объекта {@link $className}
 *
 * @return билдер для построения объекта {@link $className}
 */
public static Builder builder() {
    return new Builder();
}"""
    }

    String generateFilledStaticBuilder() {
        def statements = String.join('\n', javaFields.collect({
            "builder.$it.name = copy.$it.name;"
        }))
        """
/**
 * Создает билдер с данными из переданного объекта
 *
 * @param copy объект, данные которого копируются в билдер
 * @return билдер с данными из переданного объекта
 */
public static Builder builder($className copy) {
        Builder builder = new Builder();
        $statements
        return builder;
    }"""
    }

    private String generateBuilder() {
        new ClassBuilderGenerator(className, javaFields).generate()
    }
}
