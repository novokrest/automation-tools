import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class NodeId {

    private final String value;

    @Nonnull
    public static NodeId nodeId(@Nonnull String value) {
        return new NodeId(value);
    }

    @Nonnull
    public static Optional<NodeId> optionalNodeId(@Nullable String value) {
        return isValid(value) ? Optional.of(new NodeId(value)) : Optional.empty();
    }

    @JsonCreator
    private NodeId(String value) {
        this.value = validate(requireNonNull(value, "value"));
    }

    private static String validate(@Nonnull String value) {
        if (isValid(value)) {
            return value;
        }
        throw new RuntimeException(String.format("Invalid value: '%s'", value));
    }

    public static boolean isValid(@Nullable String value) {
        return StringUtils.isNotEmpty(value);
    }

    @JsonValue
    public String asString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        NodeId other = (NodeId) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
