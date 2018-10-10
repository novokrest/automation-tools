import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by knovokresch on 15.02.2018.
 */
class ClassDescription {

    @JsonProperty("name")
    String className
    @JsonProperty("fields")
    Map<String, String> classDescription
}

class ClassDescriptionList {

    @JsonProperty("classes")
    List<ClassDescription> classDescriptions
}