import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Created by knovokresch on 15.02.2018.
 */
class ClassDescriptionParser {

    String descriptionFile

    ClassDescriptionParser(descriptionFile) {
        this.descriptionFile = descriptionFile
    }

    ClassDescriptionList parseClasses() {
        new ObjectMapper().readValue(
                new File(descriptionFile),
                ClassDescriptionList
        )
    }
}
