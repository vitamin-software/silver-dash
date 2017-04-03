package io.vitamin.silver.dash;

import io.vitamin.silver.dash.controller.Answer;
import io.vitamin.silver.dash.controller.OrderController;
import io.vitamin.silver.dash.controller.Params;
import io.vitamin.silver.dash.service.OrderServiceImpl;
import io.vitamin.silver.dash.store.InMemoryOrderStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import static spark.Spark.*;

public class OrderControllerApp
{
     private final static Logger LOG = LoggerFactory.getLogger(OrderControllerApp.class);
     public static void main(String args[])
     {
         OrderController controller = new OrderController(new OrderServiceImpl(
                 () -> new InMemoryOrderStore<>(() -> UUID.randomUUID().toString())
         ));

         port(9999);
         path("/rest/api", () -> {
             before("/*", (req, res) -> LOG.debug("Received:{}", req));
             path("/order", () -> {
                 get("/" + Params.ORDER_ID,    new SparkAdapter(controller::getOrder));
                 post("",                      new SparkAdapter(controller::addOrder));
                 delete("/" + Params.ORDER_ID, new SparkAdapter(controller::deleteOrder));
             });
             path("/aggregates", () -> {
                 get("",  new SparkAdapter(controller::getOrderAggregates));
             });
         });
     }

     public static class SparkAdapter implements Route{
         private final BiFunction<String, Map<String, String>, Answer> underlying;

         public SparkAdapter(BiFunction<String, Map<String, String>, Answer> underlying) {
             this.underlying = underlying;
         }

         public String handle(Request request, Response response){
             Answer answer = this.underlying.apply(request.body(), request.params());
             response.status(answer.getStatusCode());
             response.type(answer.getContentType());
             return answer.getBody();
         }
     }
}
