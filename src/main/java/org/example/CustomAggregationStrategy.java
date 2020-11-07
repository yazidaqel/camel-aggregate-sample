package org.example;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        if(oldExchange == null){
            List<Contract> contracts = new ArrayList<>();

            contracts.add(newExchange.getIn().getBody(Contract.class));

            newExchange.getIn().setBody(contracts);

            return newExchange;
        }else{

            List<Contract> aggregatedContracts = oldExchange.getIn().getBody(List.class);

            Contract contractToAdd = newExchange.getIn().getBody(Contract.class);

            Optional<Contract> isContractAlreadyAggregated = aggregatedContracts.stream()
                    .filter(c -> StringUtils.equals(contractToAdd.getNumber(), c.getNumber()))
                    .findFirst();

            if(isContractAlreadyAggregated.isPresent()){

                Contract existingContract = isContractAlreadyAggregated.get();

                existingContract
                        .setAmount(
                                existingContract
                                        .getAmount()
                                        .add(contractToAdd.getAmount())
                        );
            }else{
                aggregatedContracts.add(contractToAdd);
            }

            oldExchange.getIn().setBody(aggregatedContracts);

            return oldExchange;
        }

    }
}
