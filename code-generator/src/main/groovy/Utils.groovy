/**
 * @author Konstantin Novokreshchenov (knovokresch@yamoney.ru)
 * @since 21.05.2017
 */
class Utils {
    static partition(Collection<String> collection, int size) {
        def partitions = []
        int partitionCount = collection.size() / size

        partitionCount.times { partitionNumber ->
            def begin = partitionNumber * size
            def end = begin + size - 1
            partitions << collection[begin..end]
        }

        if (collection.size() % size) {
            partitions << collection[(partitionCount * size)..-1]
        }
        partitions
    }

    static fromUpperCase(String str) {
        if (str.length() == 0) {
            str
        } else {
            str.substring(0, 1).toUpperCase() + str.substring(1)
        }
    }

    static transform(List elements, Closure leadElementTransform, Closure elementTransform) {
        if (elements.size() == 0) {
            return []
        }

        def leadTransformList = [leadElementTransform(elements[0])]

        if (elements.size() == 1) {
            return leadTransformList
        }

        leadTransformList + elements[1..-1].collect({ elementTransform(it) })
    }

    static splitCamelCaseToWords(String str) {
        str.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")
    }
}
