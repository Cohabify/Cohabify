package org.ispp4.cohabify.advertisement;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import org.ispp4.cohabify.house.House;
import org.ispp4.cohabify.model.BaseEntity;

@Getter
@Setter
@Document(collection = "advertisement")
public class Advertisement extends BaseEntity {

    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    @NotBlank
    @Size(min = 1, max = 1500)
    private String description;

    @NotNull
    @PositiveOrZero
    private Float price;

    @NotNull
    @Size(min = 1, max = 5)
    private List<String> images;

    @NotNull
    @Min(0)
    @Max(6)
    private Integer tenants;

    @NotNull
    @DBRef
    private House house;

}
