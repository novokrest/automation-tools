import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by knovokresch on 16.02.2018.
 */

class EnumDescriptionList {

    @JsonProperty
    List<EnumDescription> enums
}

class EnumDescription {

    @JsonProperty
    String name

    @JsonProperty
    String javadoc

    @JsonProperty
    List<EnumValue> values
}

class EnumValue {

    @JsonProperty
    String name

    @JsonProperty
    String repr

    @JsonProperty
    String javadoc
}
