package org.ispp4.cohabify.houseRating;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRatingRepository extends MongoRepository<HouseRating, ObjectId>{

    public List<HouseRating> findByHouseAdvertisementId(ObjectId House);
    public void deleteByHouseAdvertisementId(ObjectId House);
    public List<HouseRating> findAll();
    public Optional<HouseRating> findById(ObjectId id);
    
}
