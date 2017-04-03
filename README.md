# silver-dash

A simple-micro service implementation to register orders and to aggregate them by price 

**Order** as defined in the spec.
**OrderAggregate** represents the aggregated orders according to their price.

**Store package** consists of simple in memory store implementation.
Store is responsible for the unique id generation.

**OrderService** creates the OrderAggregates and orders them.


**Flow Of The Application**
OrderController -> OrderService -> OrderStore

**OrderControllerApp** is the main class. Application can be queried like below 

curl -X POST -H 'Content-Type: application/json' -d'{"type":"SELL","userId":"T1","quantity":"4.5","price":"321"}' http://localhost:9999/rest/api/order
curl -X POST -H 'Content-Type: application/json' -d'{"type":"SELL","userId":"T2","quantity":"2.5","price":"320"}' http://localhost:9999/rest/api/order
curl -X POST -H 'Content-Type: application/json' -d'{"type":"SELL","userId":"T3","quantity":"1.0","price":"321"}' http://localhost:9999/rest/api/order
curl -X POST -H 'Content-Type: application/json' -d'{"type":"SELL","userId":"T1","quantity":"7.5","price":"330"}' http://localhost:9999/rest/api/order

curl -X POST -H 'Content-Type: application/json' -d'{"type":"BUY","userId":"T4","quantity":"2.5","price":"327"}' http://localhost:9999/rest/api/order
curl -X POST -H 'Content-Type: application/json' -d'{"type":"BUY","userId":"T5","quantity":"4.5","price":"330"}' http://localhost:9999/rest/api/order
curl -X POST -H 'Content-Type: application/json' -d'{"type":"BUY","userId":"T4","quantity":"0.5","price":"330"}' http://localhost:9999/rest/api/order


curl -X GET http://localhost:9999/rest/api/order/${orderId}

curl -X DELETE http://localhost:9999/rest/api/order/${orderId}

curl -X GET http://localhost:9999/rest/api/aggregates


    