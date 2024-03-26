package org.ispp4.cohabify.houseRating;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HouseRatingService {

    HouseRatingRepository houseRatingRepository;

    @Transactional
    public HouseRating save(HouseRating houseRating) {
        return houseRatingRepository.save(houseRating);
    }

    @Transactional
    public void deleteByUserId(ObjectId house) {
        houseRatingRepository.findByHouseAdvertisementId(house);
    }

    @Transactional
    public void deleteById(ObjectId id) {
        houseRatingRepository.deleteById(id);
    }

    @Transactional
    public HouseRating findById(ObjectId id) {
        return houseRatingRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<HouseRating> findByHouse(ObjectId house) {
        return houseRatingRepository.findByHouseAdvertisementId(house);
    }

    @Transactional
    public List<HouseRating> findAll() {
        return houseRatingRepository.findAll();
    }

    
    
}
