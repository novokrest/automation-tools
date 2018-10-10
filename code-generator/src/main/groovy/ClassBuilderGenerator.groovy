class ClassBuilderGenerator {
    private final String className
    private final Collection<JavaField> javaFields

    ClassBuilderGenerator(String className, Collection<JavaField> javaFields) {
        this.className = className
        this.javaFields = javaFields
    }

    String generate() {
        def fields = String.join('\n', javaFields.collect({"private $it.type $it.name;"}))
        def fieldSetters = String.join('\n', javaFields.collect({
            """@Nonnull
public Builder with${it.nameFromUpperCase}(@Nonnull $it.type $it.name) {
this.${it.name} = $it.name;
return this;
}\n""" }))
        def newInstanceArgs = String.join(', \n', javaFields.collect({"$it.name"}))
        """/**
     * Вспомогательный класс для построения объекта типа {@link $className}
     */
public static class Builder {

$fields

private Builder() {
}

$fieldSetters
/**
 * Создает новый объект типа {@link $className}
 *
 * @return новый объект типа {@link $className}
 */
@Nonnull
public $className build() {
    return new $className(\n$newInstanceArgs\n);
}
}
"""
    }
}