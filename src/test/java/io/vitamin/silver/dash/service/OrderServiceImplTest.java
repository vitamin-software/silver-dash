package io.vitamin.silver.dash.service;

import io.vitamin.silver.dash.domain.Order;
import io.vitamin.silver.dash.domain.OrderAggregate;
import io.vitamin.silver.dash.store.InMemoryOrderStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderServiceImplTest {

    private OrderService orderService;

    @Before
    public void setUp(){
        orderService = new OrderServiceImpl(() -> new InMemoryOrderStore<>(() -> UUID.randomUUID().toString()));
    }

    protected String addOrder(String userId, double quantity, BigDecimal price, Order.Type type){
        Order order = new Order(userId, quantity, price, type);
        return orderService.add(order);
    }

    private void checkNoAggregates(){
        Map<Order.Type, List<OrderAggregate>> aggregates = orderService.getAggregates();
        Assert.assertEquals(2, aggregates.size());
        Assert.assertTrue("Should contain BUY order type.", aggregates.containsKey(Order.Type.BUY));
        Assert.assertTrue("Should contain SELL order type.", aggregates.containsKey(Order.Type.SELL));

        Assert.assertEquals("Should contain no BUY order.", 0, aggregates.get(Order.Type.BUY).size());
        Assert.assertEquals("Should contain no SELL order.", 0, aggregates.get(Order.Type.SELL).size());
    }

    @Test
    public void testNoOrder(){
        checkNoAggregates();
    }

    @Test
    public void testDeleteOrderWithoutEntry(){
        Order order = orderService.remove("T-Rand");
        Assert.assertNull(order);
    }

    @Test
    public void testAddThenDeleteOrder(){
        String buyOrderId = addOrder("T1", 100, BigDecimal.ONE, Order.Type.BUY);
        String sellOrderId = addOrder("T2", 10, BigDecimal.TEN, Order.Type.SELL);
        Map<Order.Type, List<OrderAggregate>> aggregates = orderService.getAggregates();

        Assert.assertEquals("Should contain no BUY order.", 1, aggregates.get(Order.Type.BUY).size());
        Assert.assertEquals("Should contain no SELL order.", 1, aggregates.get(Order.Type.SELL).size());

        Order buyOrder = orderService.remove(buyOrderId);
        Assert.assertNotNull("Order should have been removed properly.", buyOrder);

        Order sellOrder = orderService.remove(sellOrderId);
        Assert.assertNotNull("Order should have been removed properly.", sellOrder);

        checkNoAggregates();
    }

    @Test
    public void testSingleBuyOrder(){
        addOrder("T1", 100, BigDecimal.ONE, Order.Type.BUY);

        Map<Order.Type, List<OrderAggregate>> aggregates = orderService.getAggregates();
        Assert.assertEquals(2, aggregates.size());
        Assert.assertTrue("Should contain BUY order type.", aggregates.containsKey(Order.Type.BUY));
        Assert.assertTrue("Should contain SELL order type.", aggregates.containsKey(Order.Type.SELL));

        Assert.assertEquals("Should contain 1 BUY order.", 1, aggregates.get(Order.Type.BUY).size());
        Assert.assertEquals("Should contain no SELL order.", 0, aggregates.get(Order.Type.SELL).size());

        OrderAggregate aggregate = aggregates.get(Order.Type.BUY).get(0);
        Assert.assertEquals(BigDecimal.ONE, aggregate.getPrice());
        Assert.assertEquals(100, aggregate.getQuantity(), 1e-5);
    }

    @Test
    public void testSingleSaleOrder(){
        addOrder("T1", 100, BigDecimal.TEN, Order.Type.SELL);

        Map<Order.Type, List<OrderAggregate>> aggregates = orderService.getAggregates();
        Assert.assertEquals(2, aggregates.size());
        Assert.assertTrue("Should contain BUY order type.", aggregates.containsKey(Order.Type.BUY));
        Assert.assertTrue("Should contain SELL order type.", aggregates.containsKey(Order.Type.SELL));

        Assert.assertEquals("Should contain no BUY order.", 0, aggregates.get(Order.Type.BUY).size());
        Assert.assertEquals("Should contain 1 SELL order.", 1, aggregates.get(Order.Type.SELL).size());

        OrderAggregate aggregate = aggregates.get(Order.Type.SELL).get(0);
        Assert.assertEquals(BigDecimal.TEN, aggregate.getPrice());
        Assert.assertEquals(100, aggregate.getQuantity(), 1e-5);

    }

    @Test
    public void testMultipleBuyOrder(){
        addOrder("T1", 3, BigDecimal.TEN, Order.Type.BUY);
        addOrder("T1", 50, BigDecimal.ONE, Order.Type.BUY);
        addOrder("T2", 100, BigDecimal.ZERO, Order.Type.BUY);
        addOrder("T3", 7, BigDecimal.TEN, Order.Type.BUY);
        addOrder("T1", 1000, BigDecimal.ZERO, Order.Type.BUY);

        Map<Order.Type, List<OrderAggregate>> aggregates = orderService.getAggregates();
        Assert.assertEquals(2, aggregates.size());
        Assert.assertTrue("Should contain BUY order type.", aggregates.containsKey(Order.Type.BUY));
        Assert.assertTrue("Should contain SELL order type.", aggregates.containsKey(Order.Type.SELL));

        Assert.assertEquals("Should contain no BUY order.", 3, aggregates.get(Order.Type.BUY).size());
        Assert.assertEquals("Should contain no SELL order.", 0, aggregates.get(Order.Type.SELL).size());

        List<OrderAggregate> buyAggregates = aggregates.get(Order.Type.BUY);
        OrderAggregate aggregate = buyAggregates.get(0);
        Assert.assertEquals(BigDecimal.TEN, aggregate.getPrice());
        Assert.assertEquals(10, aggregate.getQuantity(), 1e-5);

        aggregate = buyAggregates.get(1);
        Assert.assertEquals(BigDecimal.ONE, aggregate.getPrice());
        Assert.assertEquals(50, aggregate.getQuantity(), 1e-5);

        aggregate = buyAggregates.get(2);
        Assert.assertEquals(BigDecimal.ZERO, aggregate.getPrice());
        Assert.assertEquals(1100, aggregate.getQuantity(), 1e-5);
    }

    @Test
    public void testMultipleSellOrder(){
        addOrder("T1", 3, BigDecimal.TEN, Order.Type.SELL);
        addOrder("T2", 50, BigDecimal.ONE, Order.Type.SELL);
        addOrder("T1", 100, BigDecimal.ZERO, Order.Type.SELL);
        addOrder("T3", 7, BigDecimal.TEN, Order.Type.SELL);
        addOrder("T4", 1000, BigDecimal.ZERO, Order.Type.SELL);

        Map<Order.Type, List<OrderAggregate>> aggregates = orderService.getAggregates();
        Assert.assertEquals(2, aggregates.size());
        Assert.assertTrue("Should contain BUY order type.", aggregates.containsKey(Order.Type.BUY));
        Assert.assertTrue("Should contain SELL order type.", aggregates.containsKey(Order.Type.SELL));

        Assert.assertEquals("Should contain no BUY order.", 0, aggregates.get(Order.Type.BUY).size());
        Assert.assertEquals("Should contain no SELL order.", 3, aggregates.get(Order.Type.SELL).size());

        List<OrderAggregate> buyAggregates = aggregates.get(Order.Type.SELL);
        OrderAggregate aggregate = buyAggregates.get(0);
        Assert.assertEquals(BigDecimal.ZERO, aggregate.getPrice());
        Assert.assertEquals(1100, aggregate.getQuantity(), 1e-5);

        aggregate = buyAggregates.get(1);
        Assert.assertEquals(BigDecimal.ONE, aggregate.getPrice());
        Assert.assertEquals(50, aggregate.getQuantity(), 1e-5);

        aggregate = buyAggregates.get(2);
        Assert.assertEquals(BigDecimal.TEN, aggregate.getPrice());
        Assert.assertEquals(10, aggregate.getQuantity(), 1e-5);
    }

    @Test
    public void testMultipleOrders(){
        addOrder("T1", 3, BigDecimal.TEN, Order.Type.SELL);
        addOrder("T2", 50, BigDecimal.ONE, Order.Type.SELL);
        addOrder("T3", 100, BigDecimal.ZERO, Order.Type.BUY);
        addOrder("T4", 7, BigDecimal.TEN, Order.Type.BUY);
        addOrder("T5", 1000, BigDecimal.ZERO, Order.Type.BUY);
        addOrder("T6", 2000, BigDecimal.ZERO, Order.Type.SELL);
        addOrder("T7", 3000, BigDecimal.ZERO, Order.Type.SELL);

        Map<Order.Type, List<OrderAggregate>> aggregates = orderService.getAggregates();
        Assert.assertEquals(2, aggregates.size());
        Assert.assertTrue("Should contain BUY order type.", aggregates.containsKey(Order.Type.BUY));
        Assert.assertTrue("Should contain SELL order type.", aggregates.containsKey(Order.Type.SELL));

        Assert.assertEquals("Should contain no BUY order.", 2, aggregates.get(Order.Type.BUY).size());
        Assert.assertEquals("Should contain no SELL order.", 3, aggregates.get(Order.Type.SELL).size());

        List<OrderAggregate> sellAggregates = aggregates.get(Order.Type.SELL);
        OrderAggregate aggregate = sellAggregates.get(0);
        Assert.assertEquals(BigDecimal.ZERO, aggregate.getPrice());
        Assert.assertEquals(5000, aggregate.getQuantity(), 1e-5);

        aggregate = sellAggregates.get(1);
        Assert.assertEquals(BigDecimal.ONE, aggregate.getPrice());
        Assert.assertEquals(50, aggregate.getQuantity(), 1e-5);

        aggregate = sellAggregates.get(2);
        Assert.assertEquals(BigDecimal.TEN, aggregate.getPrice());
        Assert.assertEquals(3, aggregate.getQuantity(), 1e-5);

        List<OrderAggregate> buyAggregates = aggregates.get(Order.Type.BUY);
        aggregate = buyAggregates.get(0);
        Assert.assertEquals(BigDecimal.TEN, aggregate.getPrice());
        Assert.assertEquals(7, aggregate.getQuantity(), 1e-5);

        aggregate = buyAggregates.get(1);
        Assert.assertEquals(BigDecimal.ZERO, aggregate.getPrice());
        Assert.assertEquals(1100, aggregate.getQuantity(), 1e-5);
    }


}
