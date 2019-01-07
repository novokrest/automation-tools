package generated;

import java.util.Objects;
import javax.annotation.Nonnull;

public class PaymentMethod {

private final String type;

private PaymentMethod(
@Nonnull String type
) {
this.type = requireNonNull(type, "type");
}

@Nonnull
public String getType() {
return type;
}

@Override
public boolean equals( Object obj) {
if (this == obj) {
return true;
}
if (obj == null || obj.getClass() != getClass()) {
return false;
}
PaymentMethod other = (PaymentMethod) obj;
return Objects.equals(type, other.type);
}

@Override
public int hashCode() {
return Objects.hash(type);
}

@Nonnull
@Override
public String toString() {
return "PaymentMethod{" +
"type='" + type + '\'' +
'}';
}

@Nonnull
public static Builder builder() {
return new Builder();
}

@Nonnull
public static Builder builder(@Nonnull PaymentMethod copy) {
Builder builder = new Builder();
builder.type = copy.type;
return builder;
}

public static class Builder {

private String type;

private Builder() {
}

@Nonnull
public Builder withType(@Nonnull String type) {
this.type = type;
return this;
}

@Nonnull
public PaymentMethod build() {
return new PaymentMethod(
type
);
}

}

}
