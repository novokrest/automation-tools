package generated;

import java.util.Objects;
import javax.annotation.Nonnull;

public class BoardPaymentRequest {

private final String orderId;

private final MonetaryAmount amount;

private final PaymentMethod paymentMethod;

private final String cardCryptogram;

private BoardPaymentRequest(
@Nonnull String orderId,
@Nonnull MonetaryAmount amount,
@Nonnull PaymentMethod paymentMethod,
@Nonnull String cardCryptogram
) {
this.orderId = requireNonNull(orderId, "orderId");
this.amount = requireNonNull(amount, "amount");
this.paymentMethod = requireNonNull(paymentMethod, "paymentMethod");
this.cardCryptogram = requireNonNull(cardCryptogram, "cardCryptogram");
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
BoardPaymentRequest other = (BoardPaymentRequest) obj;
return Objects.equals(orderId, other.orderId) &&
Objects.equals(amount, other.amount) &&
Objects.equals(paymentMethod, other.paymentMethod) &&
Objects.equals(cardCryptogram, other.cardCryptogram);
}

@Override
public int hashCode() {
return Objects.hash(orderId, amount, paymentMethod, cardCryptogram);
}

@Nonnull
@Override
public String toString() {
return "BoardPaymentRequest{" +
"orderId='" + orderId + '\'' +
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
public static Builder builder(@Nonnull BoardPaymentRequest copy) {
Builder builder = new Builder();
builder.orderId = copy.orderId;
builder.amount = copy.amount;
builder.paymentMethod = copy.paymentMethod;
builder.cardCryptogram = copy.cardCryptogram;
return builder;
}

public static class Builder {

private String orderId;
private MonetaryAmount amount;
private PaymentMethod paymentMethod;
private String cardCryptogram;

private Builder() {
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
public BoardPaymentRequest build() {
return new BoardPaymentRequest(
orderId,
amount,
paymentMethod,
cardCryptogram
);
}

}

}
