package org.ispp4.cohabify.dto;

import org.ispp4.cohabify.houseAdvertisement.HouseAdvertisement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseAdvertisementRatingRequest {

    @NotNull
    private HouseAdvertisement houseAdvertisement;

    @NotBlank
    @Size(max = 400)
    private String comment;

    private Integer rating;


}
