package org.example;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyRouteBuilder.class);

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {



        from("file:src/dataIn")
                .routeId("camel-aggregation-strategy")
                .split(body().tokenize(), new CustomAggregationStrategy())
                    .unmarshal()
                    .bindy(BindyType.Csv, Contract.class)
                    .process(exchange -> {
                        LOGGER.info("Processing record (Index={},Data={})", exchange.getIn().getHeader(Exchange.SPLIT_INDEX), exchange.getIn().getBody());
                    })
                    .marshal()
                    .bindy(BindyType.Csv, Contract.class)
                .end()
                .to("file:src/dataOut")
                .end();


    }

}
