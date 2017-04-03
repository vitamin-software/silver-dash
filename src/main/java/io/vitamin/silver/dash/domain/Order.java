package io.vitamin.silver.dash.domain;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.util.Objects;

public class Order implements Validable {
    public enum Type {
        BUY,
        SELL;
    }

    private String userId;
    private double quantity;
    private BigDecimal price;
    private Type type;

    protected Order(){}

    public Order(String userId, double quantity, BigDecimal price, Type type) {
        this.userId = userId;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public double getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Type getType() {
        return type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean isValid() {
        return !Strings.isNullOrEmpty(getUserId()) &&
                getQuantity() > 0 &&
                Objects.nonNull(getType()) &&
                Objects.nonNull(getPrice()) && BigDecimal.ZERO.compareTo(getPrice()) == -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(order.quantity, quantity) == 0 &&
                Objects.equals(userId, order.userId) &&
                Objects.equals(price, order.price) &&
                type == order.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, quantity, price, type);
    }

    @Override
    public String toString() {
        return "Order{" +
                "userId='" + userId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
