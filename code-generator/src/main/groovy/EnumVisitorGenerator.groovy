import java.time.ZonedDateTime

/**
 * Created by knovokresch on 30.03.2018.
 */
public class EnumVisitorGenerator {

    public void generate(EnumDescription enumm) {
        def now = ZonedDateTime.now()
        def visitorName = "${enumm.name}Visitor"
        def methodDef = {EnumValue value -> """
        /**
         * Вызывается при посещении {@link ${enumm.name}#${value.name}}
         *
         * @return результат посещения
         */
        V ${Utils.toCamelCase(value.name.toLowerCase())}();
"""}
        def methodDefs = enumm.values.collect{EnumValue value -> methodDef(value)}.join('')

        def definition = """
/**
 * Посетитель элементов перечисления {@link ${enumm.name}}.
 * Каждому элементу перечисления соответствует свой метод Посетителя.
 *
 * @param <V> тип возвращаемого значения при посещении
 *
 * @author Konstantin Novokreshchenov (knovokresch@yamoney.ru)
 * @since ${now.dayOfMonth}.${now.monthValue < 10 ? "0${now.monthValue}" : now.monthValue}.${now.year}
 */
public interface ${visitorName}<V> {
    ${methodDefs}
}
"""
        Utils.toFile("out/" + visitorName, definition)
    }
}
