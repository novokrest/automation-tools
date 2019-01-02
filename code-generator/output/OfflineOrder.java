package generated;

import java.util.Objects;
import javax.annotation.Nonnull;

public class OfflineOrder {

private final Long id;

private final String orderId;

private final BigDecimal amount;

private final PayCashCurrency currency;

private final String paymentMethod;

private final String cardCryptogram;

private OfflineOrder(
@Nonnull Long id,
@Nonnull String orderId,
@Nonnull BigDecimal amount,
@Nonnull PayCashCurrency currency,
@Nonnull String paymentMethod,
@Nonnull String cardCryptogram
) {
this.id = id;
this.orderId = orderId;
this.amount = amount;
this.currency = currency;
this.paymentMethod = paymentMethod;
this.cardCryptogram = cardCryptogram;
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
public BigDecimal getAmount() {
return amount;
}

@Nonnull
public PayCashCurrency getCurrency() {
return currency;
}

@Nonnull
public String getPaymentMethod() {
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
OfflineOrder other = (OfflineOrder) obj;
return Objects.equals(id, other.id) &&
Objects.equals(orderId, other.orderId) &&
Objects.equals(amount, other.amount) &&
Objects.equals(currency, other.currency) &&
Objects.equals(paymentMethod, other.paymentMethod) &&
Objects.equals(cardCryptogram, other.cardCryptogram);
}

@Override
public int hashCode() {
return Objects.hash(id, orderId, amount, currency, paymentMethod, cardCryptogram);
}

@Nonnull
@Override
public String toString() {
return "OfflineOrder{" +
"id=" + id +
", orderId='" + orderId + '\'' +
", amount=" + amount +
", currency=" + currency +
", paymentMethod='" + paymentMethod + '\'' +
", cardCryptogram='" + cardCryptogram + '\'' +
'}';
}

@Nonnull
public static Builder builder() {
return new Builder();
}

@Nonnull
public static Builder builder(@Nonnull OfflineOrder copy) {
Builder builder = new Builder();
builder.id = copy.id;
builder.orderId = copy.orderId;
builder.amount = copy.amount;
builder.currency = copy.currency;
builder.paymentMethod = copy.paymentMethod;
builder.cardCryptogram = copy.cardCryptogram;
return builder;
}

public static class Builder {

private Long id;
private String orderId;
private BigDecimal amount;
private PayCashCurrency currency;
private String paymentMethod;
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
public Builder withAmount(@Nonnull BigDecimal amount) {
this.amount = amount;
return this;
}

@Nonnull
public Builder withCurrency(@Nonnull PayCashCurrency currency) {
this.currency = currency;
return this;
}

@Nonnull
public Builder withPaymentMethod(@Nonnull String paymentMethod) {
this.paymentMethod = paymentMethod;
return this;
}

@Nonnull
public Builder withCardCryptogram(@Nonnull String cardCryptogram) {
this.cardCryptogram = cardCryptogram;
return this;
}

@Nonnull
public OfflineOrder build() {
return new OfflineOrder(
id,
orderId,
amount,
currency,
paymentMethod,
cardCryptogram
);
}

}

}
