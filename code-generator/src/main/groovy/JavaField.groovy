class JavaField {
    final String type
    final String name
    final String columnName

    static JavaFieldNameTransformer nameTransformer = new JavaFieldNameTransformer()

    static String transformName(String name) {
        nameTransformer.transform(name)
    }

    JavaField(String type, String name, String columnName) {
        this.type = type
        this.name = name
        this.columnName = columnName
    }

    String getNameFromUpperCase() {
        Utils.fromUpperCase(name)
    }

    String toString() {
        "{ type=$type, name=$name }"
    }
}