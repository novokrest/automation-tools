package generated;

import java.util.Objects;
import javax.annotation.Nonnull;

public class MonetaryAmount {

private final BigDecimal amount;

private final PayCashCurrency currency;

private MonetaryAmount(
@Nonnull BigDecimal amount,
@Nonnull PayCashCurrency currency
) {
this.amount = requireNonNull(amount, "amount");
this.currency = requireNonNull(currency, "currency");
}

@Nonnull
public BigDecimal getAmount() {
return amount;
}

@Nonnull
public PayCashCurrency getCurrency() {
return currency;
}

@Override
public boolean equals( Object obj) {
if (this == obj) {
return true;
}
if (obj == null || obj.getClass() != getClass()) {
return false;
}
MonetaryAmount other = (MonetaryAmount) obj;
return Objects.equals(amount, other.amount) &&
Objects.equals(currency, other.currency);
}

@Override
public int hashCode() {
return Objects.hash(amount, currency);
}

@Nonnull
@Override
public String toString() {
return "MonetaryAmount{" +
"amount=" + amount +
", currency=" + currency +
'}';
}

@Nonnull
public static Builder builder() {
return new Builder();
}

@Nonnull
public static Builder builder(@Nonnull MonetaryAmount copy) {
Builder builder = new Builder();
builder.amount = copy.amount;
builder.currency = copy.currency;
return builder;
}

public static class Builder {

private BigDecimal amount;
private PayCashCurrency currency;

private Builder() {
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
public MonetaryAmount build() {
return new MonetaryAmount(
amount,
currency
);
}

}

}
