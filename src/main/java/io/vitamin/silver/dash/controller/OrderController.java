package io.vitamin.silver.dash.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vitamin.silver.dash.domain.Order;
import io.vitamin.silver.dash.domain.OrderAggregate;
import io.vitamin.silver.dash.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.List;
import java.util.Map;

public class OrderController
{
    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public Answer addOrder(String requestBody, Map<String, String> queryParams){
        try {
            Order order = mapper.readValue(requestBody, Order.class);
            if (order.isValid()) {
                String orderId = orderService.add(order);
                return new Answer(HttpStatus.OK, orderId);
            }
        } catch (Exception ex) {
            LOG.error("Issue while adding order.", ex);
        }
        return new Answer(HttpStatus.BAD_REQUEST, "Unexpected Order entry.");
    }

    public Answer deleteOrder(String requestBody, Map<String, String> queryParams){
        String orderId = queryParams.get(Params.ORDER_ID);
        HttpStatus status = HttpStatus.NOT_FOUND;
        if (!StringUtils.isBlank(orderId)) {
            Order order = orderService.remove(orderId);
            status = order != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        }
        return new Answer(status);
    }

    public Answer getOrder(String requestBody, Map<String, String> queryParams){
        String orderId = queryParams.get(Params.ORDER_ID);
        HttpStatus status = HttpStatus.NOT_FOUND;
        try{
            if (!StringUtils.isBlank(orderId)) {
                Order order = orderService.get(orderId);
                status = order != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;

                return new Answer(status, mapper.writeValueAsString(order));
            }
        }catch (JsonProcessingException jse){
            LOG.error("Error while parsing order.", jse);
            status = HttpStatus.INTERNAL_ERROR;
        }
        return new Answer(status);
    }

    public Answer getOrderAggregates(String requestBody, Map<String, String> queryParams){
        try {
            Map<Order.Type, List<OrderAggregate>> aggregates = this.orderService.getAggregates();
            return Answer.ok(mapper.writeValueAsString(aggregates));
        }catch (JsonProcessingException jse){
            LOG.error("Error while parsing order.", jse);
            return new Answer(HttpStatus.INTERNAL_ERROR);
        }
    }
}
