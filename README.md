# silver-dash

A micro service to register orders and to aggregate them by price 

**Order** as defined in the spec.

**OrderAggregate** represents the aggregated orders according to their price.

**Store package** consists of simple in memory store implementation.
Store is responsible for the unique id generation.

**OrderService** creates the OrderAggregates and orders them.


**Flow Of The Application**
OrderController -> OrderService -> OrderStore

**OrderControllerApp** is the main class. 

Application can be queried like below 

Adding a SELL:
curl -X POST -H 'Content-Type: application/json' -d'{"type":"SELL","userId":"T1","quantity":"4.5","price":"321"}' http://localhost:9999/rest/api/order

Adding a BUY:
curl -X POST -H 'Content-Type: application/json' -d'{"type":"BUY","userId":"T4","quantity":"2.5","price":"327"}' http://localhost:9999/rest/api/order

Getting the order by id:
curl -X GET http://localhost:9999/rest/api/order/${orderId}

Deleting the order by id:
curl -X DELETE http://localhost:9999/rest/api/order/${orderId}

Getting dash summary:
curl -X GET http://localhost:9999/rest/api/aggregates


    
