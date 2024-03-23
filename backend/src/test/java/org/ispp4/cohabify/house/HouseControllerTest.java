package org.ispp4.cohabify.house;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.ispp4.cohabify.tag.Tag;
import org.ispp4.cohabify.tag.TagService;
import org.ispp4.cohabify.user.Gender;
import org.ispp4.cohabify.user.Plan;
import org.ispp4.cohabify.user.User;
import org.ispp4.cohabify.user.UserService;
import org.ispp4.cohabify.userAdvertisement.UserAdvertisement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;

import jakarta.validation.Valid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HouseControllerTest {
    
    @Autowired
    private HouseService houseService;

    @Autowired
    private HouseController houseController;

    @Autowired
    private UserService userService;
    
    @Autowired
    private TagService tagService;

    @Test
    public void testCreateExistingHouse() {
        House existHouse = houseService.findAll().get(0);
        ResponseEntity<House> response = houseController.createHouse(existHouse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testCreateNewHouseWithCorrectData() {
        List<String> authorities = new ArrayList<String>();
        authorities.add("User");
        User user = new User("test", "test", null, null, "test@gmail.com", null, "descripcion test", Plan.BASIC, Gender.FEMENINO, null, true, authorities, null);
        user.setIsOwner(false);
        user.setPhone("666777666");
        userService.save(user);

        House newHouse = new House();
        newHouse.setRoomsNumber(3);
        newHouse.setBathroomsNumber(2);
        newHouse.setArea(100);
        newHouse.setHeating(Heating.AIR_CONDITIONING);
        newHouse.setFloor(1);
        newHouse.setLocation("Test Location");
        newHouse.setLocationPoint(new GeoJsonPoint(40.416775, -3.703790));
        newHouse.setCadastre("12345678901233434455");
        List<Tag> tagsRequest = new ArrayList<Tag>();
        Tag tagRequest = tagService.findAll().get(0);
        tagsRequest.add(tagRequest);
        List<User> usersRequest = new ArrayList<User>();
        usersRequest.add(user);
        newHouse.setTags(tagsRequest);
        newHouse.setLikes(usersRequest);

        ResponseEntity<House> response = houseController.createHouse(newHouse);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newHouse, response.getBody());

        houseService.deleteById(newHouse.getId());
        userService.deleteById(user.getId());
    }

    @Test
    public void testRegisterWithEmptyForm() {
        House newHouse = new House();
        ResponseEntity<House> response = houseController.createHouse(newHouse);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
    }

    @Test
    public void testRegisterWithOutOfRangeValues() {
        House newHouse = new House();
        newHouse.setRoomsNumber(-1);
        newHouse.setCadastre("123456789012345678901234567890"); // Cadastre más largo de lo permitido
        ResponseEntity<House> response = houseController.createHouse(newHouse);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testRegisterWithTextFieldsExceedingLimits() {
        House newHouse = new House();
        String longString = new String(new char[1001]).replace("\0", "a");
        newHouse.setLocation(longString); 
        ResponseEntity<House> response = houseController.createHouse(newHouse);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
