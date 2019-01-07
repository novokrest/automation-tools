package generated;

import java.util.Objects;
import javax.annotation.Nonnull;

public class PaymentOrderEntity {

private final Long id;

private final String orderId;

private final MonetaryAmount amount;

private final PaymentMethod paymentMethod;

private final String cardCryptogram;

private PaymentOrderEntity(
@Nonnull Long id,
@Nonnull String orderId,
@Nonnull MonetaryAmount amount,
@Nonnull PaymentMethod paymentMethod,
@Nonnull String cardCryptogram
) {
this.id = requireNonNull(id, "id");
this.orderId = requireNonNull(orderId, "orderId");
this.amount = requireNonNull(amount, "amount");
this.paymentMethod = requireNonNull(paymentMethod, "paymentMethod");
this.cardCryptogram = requireNonNull(cardCryptogram, "cardCryptogram");
}

@Nonnull
public Long getId() {
return id;
}

@Nonnull
public String getOrderId() {
return orderId;
}

@Nonnull
public MonetaryAmount getAmount() {
return amount;
}

@Nonnull
public PaymentMethod getPaymentMethod() {
return paymentMethod;
}

@Nonnull
public String getCardCryptogram() {
return cardCryptogram;
}

@Override
public boolean equals( Object obj) {
if (this == obj) {
return true;
}
if (obj == null || obj.getClass() != getClass()) {
return false;
}
PaymentOrderEntity other = (PaymentOrderEntity) obj;
return Objects.equals(id, other.id) &&
Objects.equals(orderId, other.orderId) &&
Objects.equals(amount, other.amount) &&
Objects.equals(paymentMethod, other.paymentMethod) &&
Objects.equals(cardCryptogram, other.cardCryptogram);
}

@Override
public int hashCode() {
return Objects.hash(id, orderId, amount, paymentMethod, cardCryptogram);
}

@Nonnull
@Override
public String toString() {
return "PaymentOrderEntity{" +
"id=" + id +
", orderId='" + orderId + '\'' +
", amount=" + amount +
", paymentMethod=" + paymentMethod +
", cardCryptogram='" + cardCryptogram + '\'' +
'}';
}

@Nonnull
public static Builder builder() {
return new Builder();
}

@Nonnull
public static Builder builder(@Nonnull PaymentOrderEntity copy) {
Builder builder = new Builder();
builder.id = copy.id;
builder.orderId = copy.orderId;
builder.amount = copy.amount;
builder.paymentMethod = copy.paymentMethod;
builder.cardCryptogram = copy.cardCryptogram;
return builder;
}

public static class Builder {

private Long id;
private String orderId;
private MonetaryAmount amount;
private PaymentMethod paymentMethod;
private String cardCryptogram;

private Builder() {
}

@Nonnull
public Builder withId(@Nonnull Long id) {
this.id = id;
return this;
}

@Nonnull
public Builder withOrderId(@Nonnull String orderId) {
this.orderId = orderId;
return this;
}

@Nonnull
public Builder withAmount(@Nonnull MonetaryAmount amount) {
this.amount = amount;
return this;
}

@Nonnull
public Builder withPaymentMethod(@Nonnull PaymentMethod paymentMethod) {
this.paymentMethod = paymentMethod;
return this;
}

@Nonnull
public Builder withCardCryptogram(@Nonnull String cardCryptogram) {
this.cardCryptogram = cardCryptogram;
return this;
}

@Nonnull
public PaymentOrderEntity build() {
return new PaymentOrderEntity(
id,
orderId,
amount,
paymentMethod,
cardCryptogram
);
}

}

}
