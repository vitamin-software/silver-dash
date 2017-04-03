package io.vitamin.silver.dash.store;

import io.vitamin.silver.dash.domain.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class InMemoryDataStoreTest {
    private Store<String, Order> orderStore;

    @Before
    public void setUp(){
        orderStore = new InMemoryOrderStore<>(() -> UUID.randomUUID().toString());
    }

    protected Order newOrder(String userId, double quantity, BigDecimal price, Order.Type type){
        return new Order(userId, quantity, price, type);
    }

    @Test
    public void testEmptyStore(){
        Assert.assertEquals("There should be no Order", Collections.emptyList(), orderStore.findAll());
    }

    @Test
    public void testAddOrder(){
        Order buyOrder = newOrder("T1", 100, BigDecimal.ONE, Order.Type.BUY);
        orderStore.add(buyOrder);
        Assert.assertEquals("There should be 1 Order", 1, orderStore.findAll().size());
        Assert.assertEquals(Optional.of(buyOrder), orderStore.findAll().stream().findFirst());
    }

    @Test
    public void testAddThenGetOrder(){
        Order buyOrder = newOrder("T1", 100, BigDecimal.ONE, Order.Type.BUY);
        String id = orderStore.add(buyOrder);
        Assert.assertEquals(buyOrder, orderStore.find(id));
    }

    @Test
    public void testAddThenDeleteOrder(){
        Order buyOrder = newOrder("T1", 100, BigDecimal.ONE, Order.Type.BUY);
        String buyOrderId = orderStore.add(buyOrder);
        Assert.assertEquals("There should be 1 Order", 1, orderStore.findAll().size());
        Assert.assertEquals(Optional.of(buyOrder), orderStore.findAll().stream().findFirst());

        Order order = orderStore.remove(buyOrderId);
        Assert.assertEquals(buyOrder, order);
        Assert.assertEquals("There should be no Order", 0, orderStore.findAll().size());
    }
}
