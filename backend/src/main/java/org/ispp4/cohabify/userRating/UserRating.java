package org.ispp4.cohabify.userRating;

import org.ispp4.cohabify.model.BaseEntity;
import org.ispp4.cohabify.user.User;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@Document(collection = "userRatings")
public class UserRating extends BaseEntity {
    
    @DBRef
    private User user;

    @DBRef
    private User ratedUser;

    @Size(max = 400)
    private String comment;

    public UserRating() {
        super();
    }

    @Override
    public String toString() {
        return "UserRating{" +
            "userId='" + user.getId() + '\'' +
            ", ratedUserId='" + ratedUser.getId() + '\'' +
            ", comment='" + comment + '\'' +
            '}';
    }


}
