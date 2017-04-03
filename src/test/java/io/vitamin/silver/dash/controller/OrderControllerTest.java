package io.vitamin.silver.dash.controller;

import io.vitamin.silver.dash.service.OrderServiceImpl;
import io.vitamin.silver.dash.store.InMemoryOrderStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

public class OrderControllerTest {
    private OrderController orderController;

    @Before
    public void setUp(){
        orderController = new OrderController(new OrderServiceImpl(
                () -> new InMemoryOrderStore<>(() -> UUID.randomUUID().toString())
        ));
    }

    @Test
    public void testDeleteNotExistingOrder(){
        Answer answer = orderController.deleteOrder("", Collections.singletonMap(Params.ORDER_ID, "1234"));
        Assert.assertEquals(HttpStatus.NOT_FOUND.intValue(), answer.getStatusCode());
    }

    @Test
    public void testGetNotExistingOrder(){
        Answer answer = orderController.getOrder("", Collections.singletonMap(Params.ORDER_ID, "1234"));
        Assert.assertEquals(HttpStatus.NOT_FOUND.intValue(), answer.getStatusCode());
    }

    @Test
    public void testAddOrder(){
        String order = "{\"type\":\"SELL\",\"userId\":\"T1\",\"quantity\":\"4.5\",\"price\":\"321\"}";
        Answer answer = orderController.addOrder(order, Collections.emptyMap());
        Assert.assertEquals(HttpStatus.OK.intValue(), answer.getStatusCode());
        Assert.assertNotNull(answer.getBody());
    }

    @Test
    public void testAddThenDeleteOrder(){
        String order = "{\"type\":\"SELL\",\"userId\":\"T1\",\"quantity\":\"4.5\",\"price\":\"321\"}";
        Answer answer = orderController.addOrder(order, Collections.emptyMap());
        Assert.assertEquals(HttpStatus.OK.intValue(), answer.getStatusCode());

        answer = orderController.deleteOrder("", Collections.singletonMap(Params.ORDER_ID, answer.getBody()));
        Assert.assertEquals(HttpStatus.OK.intValue(), answer.getStatusCode());
    }

    private void checkAgainstInvalidOrder(String order){
        Answer answer = orderController.addOrder(order, Collections.emptyMap());
        Assert.assertEquals(HttpStatus.BAD_REQUEST.intValue(), answer.getStatusCode());
        Assert.assertEquals("Unexpected Order entry.", answer.getBody());
    }

    @Test
    public void testAddInvalidOrder(){
        checkAgainstInvalidOrder("{\"userId\":\"T1\",\"quantity\":\"4.5\",\"price\":\"321\"}");
        checkAgainstInvalidOrder("{\"type\":\"SELL\",\"quantity\":\"4.5\",\"price\":\"321\"}");
        checkAgainstInvalidOrder("{\"type\":\"SELL\",\"userId\":\"T1\",\"price\":\"321\"}");
        checkAgainstInvalidOrder("{\"type\":\"SELL\",\"userId\":\"T1\",\"quantity\":\"4.5\"}");
        checkAgainstInvalidOrder("{\"type\":\"SELL\",\"userId\":\"T1\",\"quantity\":\"-4.5\",\"price\":\"321\"}");
        checkAgainstInvalidOrder("{\"type\":\"SELL\",\"userId\":\"T1\",\"quantity\":\"4.5\",\"price\":\"-321\"}");
        checkAgainstInvalidOrder("{\"type\":\"SELL\",\"userId\":\"T1\",\"quantity\":\"0\",\"price\":\"321\"}");
    }

    @Test
    public void testOrderAggregateBeforeAnyOrder(){
        Answer answer = orderController.getOrderAggregates("", Collections.emptyMap());
        Assert.assertEquals(HttpStatus.OK.intValue(), answer.getStatusCode());
        Assert.assertEquals("{\"BUY\":[],\"SELL\":[]}", answer.getBody());
    }

    @Test
    public void testOrderAggregates(){
        String s1 = "{\"type\":\"SELL\",\"userId\":\"T1\",\"quantity\":\"1.5\",\"price\":\"123\"}";
        String s2 = "{\"type\":\"SELL\",\"userId\":\"T2\",\"quantity\":\"2.5\",\"price\":\"456\"}";
        String s3 = "{\"type\":\"SELL\",\"userId\":\"T3\",\"quantity\":\"7\",\"price\":\"123\"}";
        String b1 = "{\"type\":\"BUY\",\"userId\":\"T4\",\"quantity\":\"4.5\",\"price\":\"120\"}";
        String b2 = "{\"type\":\"BUY\",\"userId\":\"T5\",\"quantity\":\".5\",\"price\":\"110\"}";

        Stream.of(s1, s2, s3, b1, b2)
                .map(o -> orderController.addOrder(o, Collections.emptyMap()))
                .forEach(answer -> Assert.assertEquals(HttpStatus.OK.intValue(), answer.getStatusCode()));
        Answer answer = orderController.getOrderAggregates("", Collections.emptyMap());
        Assert.assertEquals(HttpStatus.OK.intValue(), answer.getStatusCode());
        Assert.assertEquals("{\"BUY\":[{\"quantity\":4.5,\"price\":120},{\"quantity\":0.5,\"price\":110}],\"SELL\":[{\"quantity\":8.5,\"price\":123},{\"quantity\":2.5,\"price\":456}]}", answer.getBody());
    }
}
