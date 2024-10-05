package com.example.backend_voltix.repository.mongo;

import com.example.backend_voltix.dto.mongo.ConsumptionGroupResponseDto;
import com.example.backend_voltix.model.mongo.Consommation;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ConsommationRepository extends MongoRepository<Consommation, String> {
    @Query("{'date' : {$gte: ?0, $lt: ?1}, 'sn': ?2}")
    List<Consommation> findByDateAndSerialNumberBetween(double start, double end, String sn);
    @Aggregation(pipeline = {
            "{ '$match': { 'sn': ?0 } }", // Match documents by serial number
            "{ '$group': { '_id': '$channelId', 'totalConsumption': { '$sum': '$consommation_diff' } } }", // Group by channelId and sum the consommation_diff
            "{ '$sort': { 'totalConsumption': -1 } }", // Sort by totalConsumption in descending order
            "{ '$limit': 1 }" // Limit the result to only the top channel
    })
    ConsumptionGroupResponseDto findGroupedConsumptionBySerialNumber(String serialNumber);
    @Aggregation(pipeline = {
            "{ '$match': { 'sn': ?0 } }",
            "{ '$group': { '_id': null, 'totalConsumption': { '$sum': '$consommation_diff' } } }"
    })
    double findTotalConsumptionBySerialNumber(String serialNumber);
    @Query("{'date' : {$gte: ?0, $lt: ?1}, 'sn': ?2, 'channelId': ?3}")
    List<Consommation> findByDateSerialNumberAndChannelId(long start, long end, String serialNumber, int channelId);

}