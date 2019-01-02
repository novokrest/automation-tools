package generated;

import java.time.ZonedDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;

public class PaymentRequestEntity {

private final Long id;

private final String request;

private final ZonedDateTime createdAt;

private final ZonedDateTime updatedAt;

private final String status;

private PaymentRequestEntity(
@Nonnull Long id,
@Nonnull String request,
@Nonnull ZonedDateTime createdAt,
@Nonnull ZonedDateTime updatedAt,
@Nonnull String status
) {
this.id = id;
this.request = request;
this.createdAt = createdAt;
this.updatedAt = updatedAt;
this.status = status;
}

@Nonnull
public Long getId() {
return id;
}

@Nonnull
public String getRequest() {
return request;
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
public String getStatus() {
return status;
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
Objects.equals(request, other.request) &&
Objects.equals(createdAt, other.createdAt) &&
Objects.equals(updatedAt, other.updatedAt) &&
Objects.equals(status, other.status);
}

@Override
public int hashCode() {
return Objects.hash(id, request, createdAt, updatedAt, status);
}

@Nonnull
@Override
public String toString() {
return "PaymentRequestEntity{" +
"id=" + id +
", request='" + request + '\'' +
", createdAt=" + createdAt +
", updatedAt=" + updatedAt +
", status='" + status + '\'' +
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
builder.request = copy.request;
builder.createdAt = copy.createdAt;
builder.updatedAt = copy.updatedAt;
builder.status = copy.status;
return builder;
}

public static class Builder {

private Long id;
private String request;
private ZonedDateTime createdAt;
private ZonedDateTime updatedAt;
private String status;

private Builder() {
}

@Nonnull
public Builder withId(@Nonnull Long id) {
this.id = id;
return this;
}

@Nonnull
public Builder withRequest(@Nonnull String request) {
this.request = request;
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
public Builder withStatus(@Nonnull String status) {
this.status = status;
return this;
}

@Nonnull
public PaymentRequestEntity build() {
return new PaymentRequestEntity(
id,
request,
createdAt,
updatedAt,
status
);
}

}

}
