# cfair.trial

in fact, the application comprises two kinds of message processors:

* __live__. It's used for massive posting of messages. All received messages are processed every second and result
of processing is sent to frontend.
* __simple__. It keeps received messages in a list on the server. This part might be used for tests of 
REST infrastructure (message validation, exception handling). Since it keeps consumed data in memory, 
it's a bad idea to use this part for massive post.


## REST API

see [full documentation of REST API](http://ec2-52-11-167-61.us-west-2.compute.amazonaws.com:8080/cfair/doc/)

### end points

base path is `/cfair`

* `POST /service` - Use it for post of large number of messages. Postpones message
validation and responses with success `(204)` status regardless of validation result. Silently ignores invalid messages.
* `POST /service/add` - consumes messages synchronously. Use it for test of REST functionality. Responses with success (204)
status or ExceptionResponse in case of validation failure.
(see [REST API doc](http://ec2-52-11-167-61.us-west-2.compute.amazonaws.com:8080/cfair/doc/))
* `POST /service/addasync` - consumes messages __a__synchronously. Use it for test of REST functionality. Postpones message
validation and responses with success `(204)` status regardless of validation result. Silently ignores invalid messages.
* `GET /service/list` - responds with list of messages cosumed by `/service/add` and `/service/addasync` endpoints.

### data validation

* rate range is `[0.5,1.5]`
* user ID range is `[1,)`
* amount buy|sell range is `[0.0,)`
* currencies codes should conform to `ISO 4217`, `currencyFrom` and `currencyTo` attributes shouldn't equal
* country codes should conform to `ISO 3166`
* you required dates to be presented in format `dd-MMM-yy HH:mm:ss`, which does not conform to any standard format `(ISO 8601)`.
In spite of that the backend is designed to accept this data format (for example `14-Jan-15 23:12:33`), and correctly formated
date (such as `2014-01-14T23:12:33`) will be considered as invalid. 

## frontend

is available [here](http://ec2-52-11-167-61.us-west-2.compute.amazonaws.com:8080/cfair/)

* __live__ section represents result of processing of messages consumed by `/service` endpoint (live candle stick chart). 
Actually, I'm hardly familiar with trading domain, so this is merely a demo.
* __simple__ section represents data consumed by `/service/add` and `/server/addasync` endpoints

## tools

there is the utility for loading backend with random data:

    mvn exec:java -Dexec.mainClass=com.cfair.trial.Loader -Dexec.classpathScope=test \
    		-Dexec.args="SERVICEURL THREADS_PER_PAIR [[CURRENCYFROM/CURRENCYTO]...]
    
* `SERVICEURL` - endpoint. Use `http://ec2-52-11-167-61.us-west-2.compute.amazonaws.com:8080/cfair/service`
* `THREADS_PER_PAIR` - number of threads per currency pair
* `CURRENCYFROM/CURRENCYTO` - currency pair. Multiple pairs might be specified. If no pair is specified, a generator for 
`USD/EUR` pair will be started.

example:

    mvn exec:java -Dexec.mainClass=com.cfair.trial.Loader -Dexec.classpathScope=test \
    		-Dexec.args="http://ec2-52-11-167-61.us-west-2.compute.amazonaws.com:8080/cfair/service 3 GBP/RUR USD/RUR"

this will load backend with messages of `GBR/RUR` & `USD/RUR` currency pairs, three threads per each pair.

Currently there's one such generator running. So if you want me to stop it, so that it doesn't impede your tests, please 
let me know.

