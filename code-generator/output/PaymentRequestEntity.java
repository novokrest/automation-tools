package generated;

import java.time.ZonedDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;

public class PaymentRequestEntity {

private final Long id;

private final String requestAuthHeader;

private final String requestBody;

private final String requestHash;

private final ZonedDateTime createdAt;

private final ZonedDateTime updatedAt;

private final PaymentRequestProcessStatus processStatus;

private PaymentRequestEntity(
@Nonnull Long id,
@Nonnull String requestAuthHeader,
@Nonnull String requestBody,
@Nonnull String requestHash,
@Nonnull ZonedDateTime createdAt,
@Nonnull ZonedDateTime updatedAt,
@Nonnull PaymentRequestProcessStatus processStatus
) {
this.id = id;
this.requestAuthHeader = requestAuthHeader;
this.requestBody = requestBody;
this.requestHash = requestHash;
this.createdAt = createdAt;
this.updatedAt = updatedAt;
this.processStatus = processStatus;
}

@Nonnull
public Long getId() {
return id;
}

@Nonnull
public String getRequestAuthHeader() {
return requestAuthHeader;
}

@Nonnull
public String getRequestBody() {
return requestBody;
}

@Nonnull
public String getRequestHash() {
return requestHash;
}

@Nonnull
public ZonedDateTime getCreatedAt() {
return createdAt;
}

@Nonnull
public ZonedDateTime getUpdatedAt() {
return updatedAt;
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
PaymentRequestEntity other = (PaymentRequestEntity) obj;
return Objects.equals(id, other.id) &&
Objects.equals(requestAuthHeader, other.requestAuthHeader) &&
Objects.equals(requestBody, other.requestBody) &&
Objects.equals(requestHash, other.requestHash) &&
Objects.equals(createdAt, other.createdAt) &&
Objects.equals(updatedAt, other.updatedAt) &&
Objects.equals(processStatus, other.processStatus);
}

@Override
public int hashCode() {
return Objects.hash(id, requestAuthHeader, requestBody, requestHash, createdAt, updatedAt, processStatus);
}

@Nonnull
@Override
public String toString() {
return "PaymentRequestEntity{" +
"id=" + id +
", requestAuthHeader='" + requestAuthHeader + '\'' +
", requestBody='" + requestBody + '\'' +
", requestHash='" + requestHash + '\'' +
", createdAt=" + createdAt +
", updatedAt=" + updatedAt +
", processStatus=" + processStatus +
'}';
}

@Nonnull
public static Builder builder() {
return new Builder();
}

@Nonnull
public static Builder builder(@Nonnull PaymentRequestEntity copy) {
Builder builder = new Builder();
builder.id = copy.id;
builder.requestAuthHeader = copy.requestAuthHeader;
builder.requestBody = copy.requestBody;
builder.requestHash = copy.requestHash;
builder.createdAt = copy.createdAt;
builder.updatedAt = copy.updatedAt;
builder.processStatus = copy.processStatus;
return builder;
}

public static class Builder {

private Long id;
private String requestAuthHeader;
private String requestBody;
private String requestHash;
private ZonedDateTime createdAt;
private ZonedDateTime updatedAt;
private PaymentRequestProcessStatus processStatus;

private Builder() {
}

@Nonnull
public Builder withId(@Nonnull Long id) {
this.id = id;
return this;
}

@Nonnull
public Builder withRequestAuthHeader(@Nonnull String requestAuthHeader) {
this.requestAuthHeader = requestAuthHeader;
return this;
}

@Nonnull
public Builder withRequestBody(@Nonnull String requestBody) {
this.requestBody = requestBody;
return this;
}

@Nonnull
public Builder withRequestHash(@Nonnull String requestHash) {
this.requestHash = requestHash;
return this;
}

@Nonnull
public Builder withCreatedAt(@Nonnull ZonedDateTime createdAt) {
this.createdAt = createdAt;
return this;
}

@Nonnull
public Builder withUpdatedAt(@Nonnull ZonedDateTime updatedAt) {
this.updatedAt = updatedAt;
return this;
}

@Nonnull
public Builder withProcessStatus(@Nonnull PaymentRequestProcessStatus processStatus) {
this.processStatus = processStatus;
return this;
}

@Nonnull
public PaymentRequestEntity build() {
return new PaymentRequestEntity(
id,
requestAuthHeader,
requestBody,
requestHash,
createdAt,
updatedAt,
processStatus
);
}

}

}
