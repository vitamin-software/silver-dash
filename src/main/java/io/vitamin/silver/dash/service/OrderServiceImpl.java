package io.vitamin.silver.dash.service;

import com.google.common.collect.ImmutableMap;
import io.vitamin.silver.dash.domain.Order;
import io.vitamin.silver.dash.domain.OrderAggregate;
import io.vitamin.silver.dash.store.Store;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {
    private final Map<Order.Type, Store<String, Order>> orderStores;

    public OrderServiceImpl(Supplier<Store<String, Order>> orderStoreSupplier) {
        orderStores = new HashMap<>();
        Arrays.stream(Order.Type.values()).forEach(type -> orderStores.put(type, orderStoreSupplier.get()));
    }

    @Override
    public Order get(String orderId) {
        return execute(os -> os.find(orderId));
    }

    @Override
    public Order remove(String orderId) {
        return execute(os -> os.remove(orderId));
    }

    protected Order execute(Function<Store<String, Order>, Order> fx) {
        return orderStores.values().stream()
                .map(fx)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }


    @Override
    public String add(Order order) {
        return orderStores.get(order.getType()).add(order);
    }

    @Override
    public Map<Order.Type, List<OrderAggregate>> getAggregates() {
        List<OrderAggregate> buyAggregates = toAggregates(orderStores.get(Order.Type.BUY).findAll(),
                Comparator.comparing(OrderAggregate::getPrice));

        Collections.reverse(buyAggregates);

        List<OrderAggregate> sellAggregates = toAggregates(orderStores.get(Order.Type.SELL).findAll(),
                Comparator.comparing(OrderAggregate::getPrice));

        return ImmutableMap.of(
                Order.Type.BUY, buyAggregates,
                Order.Type.SELL, sellAggregates
        );
    }

    protected List<OrderAggregate> toAggregates(Collection<Order> orders, Comparator<OrderAggregate> comparator) {
        Map<BigDecimal, List<Order>> byPrice = orders.stream()
                .collect(Collectors.groupingBy(Order::getPrice));

        return byPrice.entrySet().stream()
                .map(e -> toAggregate(e.getValue()))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    protected OrderAggregate toAggregate(List<Order> orders) {
        Order infoProvider = orders.get(0);
        double quantitySum = orders.stream()
                .mapToDouble(Order::getQuantity)
                .sum();
        return new OrderAggregate(infoProvider.getPrice(), quantitySum);
    }
}
