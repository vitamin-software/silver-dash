package io.vitamin.silver.dash.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderAggregate implements Comparable<OrderAggregate> {
    private final double quantity;
    private final BigDecimal price;

    public OrderAggregate(BigDecimal price, double quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public int compareTo(OrderAggregate o) {
        return this.getPrice().compareTo(o.getPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderAggregate that = (OrderAggregate) o;
        return Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }

    @Override
    public String toString() {
        return "OrderAggregate{" +
                "price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
