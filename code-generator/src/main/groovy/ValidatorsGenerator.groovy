/**
 * Created by knovokresch on 22.02.2018.
 */
class ValidatorsGenerator {

    static void main(String[] args) {
        def descriptionFile = args.length == 0 ? 'classes.json' : args[1]
        def parser = new ClassDescriptionParser(descriptionFile)
        parser.parseClasses().classDescriptions.forEach {
            new ValidatorsGenerator().generate(it)
        }
    }

    void generate(ClassDescription clazz) {
        def validator = """

notNull(${clazz.className}.class)
${forProperties(clazz)}
"""

        Utils.toFile("out/${clazz.className}.validator.gen", validator)
    }

    String forProperties(ClassDescription clazz) {
        clazz.classDescription.collect{k,v -> forProperty(clazz.className, k, v)}.join("\n")
    }

    String forProperty(String className, String propertyName, String propertyType) {
        ".and(forProperty(${className}::get${Utils.fromUpperCase(propertyName)}, notNull(\"${propertyName}\")))"
    }
}
