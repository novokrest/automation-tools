package generated;

import java.util.Objects;
import javax.annotation.Nonnull;

public class RequestProcessContext {

private final String authorization;

private final RequestT request;

private final PaymentRequestProcessStatus processStatus;

private RequestProcessContext(
@Nonnull String authorization,
@Nonnull RequestT request,
@Nonnull PaymentRequestProcessStatus processStatus
) {
this.authorization = requireNonNull(authorization, "authorization");
this.request = requireNonNull(request, "request");
this.processStatus = requireNonNull(processStatus, "processStatus");
}

@Nonnull
public String getAuthorization() {
return authorization;
}

@Nonnull
public RequestT getRequest() {
return request;
}

@Nonnull
public PaymentRequestProcessStatus getProcessStatus() {
return processStatus;
}

@Override
public boolean equals( Object obj) {
if (this == obj) {
return true;
}
if (obj == null || obj.getClass() != getClass()) {
return false;
}
RequestProcessContext other = (RequestProcessContext) obj;
return Objects.equals(authorization, other.authorization) &&
Objects.equals(request, other.request) &&
Objects.equals(processStatus, other.processStatus);
}

@Override
public int hashCode() {
return Objects.hash(authorization, request, processStatus);
}

@Nonnull
@Override
public String toString() {
return "RequestProcessContext{" +
"authorization='" + authorization + '\'' +
", request=" + request +
", processStatus=" + processStatus +
'}';
}

@Nonnull
public static Builder builder() {
return new Builder();
}

@Nonnull
public static Builder builder(@Nonnull RequestProcessContext copy) {
Builder builder = new Builder();
builder.authorization = copy.authorization;
builder.request = copy.request;
builder.processStatus = copy.processStatus;
return builder;
}

public static class Builder {

private String authorization;
private RequestT request;
private PaymentRequestProcessStatus processStatus;

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
public Builder withProcessStatus(@Nonnull PaymentRequestProcessStatus processStatus) {
this.processStatus = processStatus;
return this;
}

@Nonnull
public RequestProcessContext build() {
return new RequestProcessContext(
authorization,
request,
processStatus
);
}

}

}
