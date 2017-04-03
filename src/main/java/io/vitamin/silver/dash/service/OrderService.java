package io.vitamin.silver.dash.service;

import io.vitamin.silver.dash.domain.Order;
import io.vitamin.silver.dash.domain.OrderAggregate;

import java.util.List;
import java.util.Map;

public interface OrderService {
    String add(Order order);
    Order remove(String orderId);
    Order get(String orderId);
    Map<Order.Type, List<OrderAggregate>> getAggregates();
}
