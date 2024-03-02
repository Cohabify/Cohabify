package org.ispp4.cohabify.userAdvertisement;

import java.time.LocalDate;
import java.util.List;
import org.bson.types.ObjectId;
import org.ispp4.cohabify.model.BaseEntity;
import org.ispp4.cohabify.user.User;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "userAdvertisements")
public class UserAdvertisement extends BaseEntity {

    private String description;
    
    @NotNull
    private Double maxBudget;

    @NotBlank
    private String desiredLocation;

    @NotNull
    private LocalDate entranceDate;

    private LocalDate exitDate;

    @NotNull
    private Integer maxCohabitants;

    @NotNull
    private List<User> likes;

    @DBRef
    @NotNull
    private User user;

    public UserAdvertisement(ObjectId id, String description, Double maxBudget, String desiredLocation, LocalDate entranceDate, LocalDate exitDate, Integer maxCohabitants, List<User> likes, User user) {
        this.id = id;
        this.description = description;
        this.maxBudget = maxBudget;
        this.desiredLocation = desiredLocation;
        this.entranceDate = entranceDate;
        this.exitDate = exitDate;
        this.maxCohabitants = maxCohabitants;
        this.likes = likes;
        this.user = user;
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", description='" + getDescription() + "'" +
            ", maxBudget='" + getMaxBudget() + "'" +
            ", desiredLocation='" + getDesiredLocation() + "'" +
            ", entranceDate='" + getEntranceDate() + "'" +
            ", exitDate='" + getExitDate() + "'" +
            ", maxCohabitants='" + getMaxCohabitants() + "'" +
            ", likes='" + getLikes() + "'" +
            ", user='" + getUser() + "'"+
            "}";
    }

}