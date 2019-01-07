package generated;

import java.util.Objects;
import javax.annotation.Nonnull;

public class ApiRequest {

private final String authorization;

private final RequestT request;

private ApiRequest(
@Nonnull String authorization,
@Nonnull RequestT request
) {
this.authorization = requireNonNull(authorization, "authorization");
this.request = requireNonNull(request, "request");
}

@Nonnull
public String getAuthorization() {
return authorization;
}

@Nonnull
public RequestT getRequest() {
return request;
}

@Override
public boolean equals( Object obj) {
if (this == obj) {
return true;
}
if (obj == null || obj.getClass() != getClass()) {
return false;
}
ApiRequest other = (ApiRequest) obj;
return Objects.equals(authorization, other.authorization) &&
Objects.equals(request, other.request);
}

@Override
public int hashCode() {
return Objects.hash(authorization, request);
}

@Nonnull
@Override
public String toString() {
return "ApiRequest{" +
"authorization='" + authorization + '\'' +
", request=" + request +
'}';
}

@Nonnull
public static Builder builder() {
return new Builder();
}

@Nonnull
public static Builder builder(@Nonnull ApiRequest copy) {
Builder builder = new Builder();
builder.authorization = copy.authorization;
builder.request = copy.request;
return builder;
}

public static class Builder {

private String authorization;
private RequestT request;

private Builder() {
}

@Nonnull
public Builder withAuthorization(@Nonnull String authorization) {
this.authorization = authorization;
return this;
}

@Nonnull
public Builder withRequest(@Nonnull RequestT request) {
this.request = request;
return this;
}

@Nonnull
public ApiRequest build() {
return new ApiRequest(
authorization,
request
);
}

}

}
