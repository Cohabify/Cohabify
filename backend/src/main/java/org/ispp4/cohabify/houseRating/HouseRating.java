package org.ispp4.cohabify.houseRating;


import java.util.Date;

import org.ispp4.cohabify.houseAdvertisement.HouseAdvertisement;
import org.ispp4.cohabify.model.BaseEntity;
import org.ispp4.cohabify.user.User;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "houseRatings")
public class HouseRating extends BaseEntity{
    
    @DBRef
    @NotNull
    private HouseAdvertisement houseAdvertisement;
   
    @DBRef
    @NotNull
    private User user;

    @NotNull
    @PositiveOrZero
    @Max(5)
    private Integer rating;

    private Date date;

    @Size(max = 400)
    private String comment;


    @Override
    public String toString() {
        return "houseRating{" +
            "house='" + houseAdvertisement + '\'' +
            ", user='" + user + '\'' +
            ", rating=" + rating +
            ", comment='" + comment + '\'' +
            "date='" + date + '\'' +
            '}';
    }
}
