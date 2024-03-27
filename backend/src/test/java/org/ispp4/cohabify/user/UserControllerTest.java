package org.ispp4.cohabify.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.ispp4.cohabify.dto.JwtTokenDto;
import org.ispp4.cohabify.dto.UserUpdateRequest;
import org.ispp4.cohabify.tag.Tag;
import org.ispp4.cohabify.tag.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.google.rpc.context.AttributeContext.Response;

import jakarta.validation.ConstraintViolationException;


@SpringBootTest
public class UserControllerTest {
    
    @Autowired
    UserService userService;

    @Autowired
    UserController userController;

    @Autowired
    TagService tagService;

    @Test
    void shouldCreateUser(){

        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);

        ResponseEntity<User> response = userController.createUser(user);
        assertEquals(new ResponseEntity<>(user, HttpStatus.CREATED), response);
        userService.deleteById(user.getId());
    }

    @Test
    void shouldNotCreateUser(){
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test000000000000000000000000000000000000000000000000000000");
        user.setPassword("test");
        user.setIsOwner(true);
        user.setPhone("test");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);    

        try{
            userController.createUser(user);
            userService.deleteById(user.getId());
        }catch(Exception e){
            assertEquals(e.getClass(), 
            ConstraintViolationException.class);
        }
    }

    @Test
    void shouldListAllUsers(){
        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assert(response.getBody().size() >= 1);
    }

    @Test
    void shouldFindById(){
        User user = new User();
        userService.save(user);
        ResponseEntity<User> response = userController.getUserById(user.getId());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), response.getBody().getId());
        userService.deleteById(user.getId());

        response = userController.getUserById(user.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFindBySecureSearch(){
        String requestBody = "{\"username\":\"test\",\"phone\":\"1234567890\"}";
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("1234567890");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        user = userService.save(user);

        ResponseEntity<User> response = userController.secureSearch(requestBody);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), response.getBody().getId());

        userService.deleteById(user.getId());

        response = userController.secureSearch(requestBody);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldGetOwners(){
        ResponseEntity<List<User>> response = userController.findOwners();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assert(response.getBody().size()>=1);
        List<User> owners = response.getBody();
        for(User u : owners){
            u.setIsOwner(false);
            userService.save(u);
        }
        response = userController.findOwners();
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);

        for(User u : owners){
            u.setIsOwner(true);
            userService.save(u);
        }
    }

    @Test
    void shoudlUpdateUser(){
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        userService.save(user);

        List<Tag> tags = new ArrayList<>();
        tags.add(tagService.findAll().get(0));
        UserUpdateRequest request = new UserUpdateRequest("x","test2","Test_1512-ao",tags,user.getDescription(),user.getEmail(),"666666666",user.getGender(),false);
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        try {
            ResponseEntity<?> response = userController.updateUser(user.getId(), request, bindingResult, null);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assert(response.getBody().getClass().equals(JwtTokenDto.class));
        } catch (IOException e) {   
            e.printStackTrace();
        }
        userService.deleteById(user.getId());
    }

    @Test
    void shouldNotUpdateUserConflictUsername(){
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        userService.save(user);

        List<Tag> tags = new ArrayList<>();
        tags.add(tagService.findAll().get(0));
        UserUpdateRequest request = new UserUpdateRequest("x","admin","Test_1512-ao",tags,user.getDescription(),user.getEmail(),"666666666",user.getGender(),false);
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        try {
            ResponseEntity<?> response = userController.updateUser(user.getId(), request, bindingResult, null);
            assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        } catch (IOException e) {   
            e.printStackTrace();
        }
        userService.deleteById(user.getId());
    }

    @Test
    void shouldNotUpdateUserInvalid(){
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        userService.save(user);

        try {
            List<Tag> tags = new ArrayList<>();
            tags.add(tagService.findAll().get(0));
            UserUpdateRequest request = new UserUpdateRequest("x","test","",tags,user.getDescription(),user.getEmail(),"test",user.getGender(),false);
            BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
            ResponseEntity<?> response = userController.updateUser(user.getId(), request, bindingResult, null);
        } catch (Exception e) {   
            assertEquals(e.getClass(), ConstraintViolationException.class);
        }
        userService.deleteById(user.getId());
    }

    @Test
    void shouldNotFindUserToUpdate(){
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        user.setId( new ObjectId("69d5ec9af682fbd39a1b8b9d"));

        List<Tag> tags = new ArrayList<>();
        tags.add(tagService.findAll().get(0));
        UserUpdateRequest request = new UserUpdateRequest("x","test2","Test_1512-ao",tags,user.getDescription(),user.getEmail(),"666666666",user.getGender(),false);
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        try {
            ResponseEntity<?> response = userController.updateUser(user.getId(), request, bindingResult, null);
            assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {   
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdatePlan(){
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        userService.save(user);

        ResponseEntity<User> response = userController.updateUserPlan(user.getId(), "basic");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getId(), response.getBody().getId());
        userService.deleteById(user.getId());
    }

    @Test
    void shouldNotUpdatePlan(){
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        userService.save(user);

        ResponseEntity<User> response = userController.updateUserPlan(user.getId(), "error");
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        
        userService.deleteById(user.getId());
        
        response = userController.updateUserPlan(user.getId(), "basic");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteUser(){
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        userService.save(user);

        ResponseEntity<HttpStatus> response = userController.deleteUser(user.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldModifyRaters(){
        List<String> authorities = new ArrayList<>();
        authorities.add("User");
        User user = new User();
        user.setUsername("test");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        userService.save(user);
        
        User user2 = new User();
        user.setUsername("test2");
        user.setPassword("Test_1512*123oa");
        user.setIsOwner(true);
        user.setPhone("666666666");
        user.setEmail("test@gmail.com");
        user.setDescription("test description");
        user.setPlan(Plan.BASIC);
        user.setGender(Gender.FEMENINO);
        user.setIsVerified(false);
        user.setAuthorities(authorities);
        userService.save(user2);

        ResponseEntity<User> response = userController.modifyRaters(user.getId(), user2.getId());
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        userService.deleteById(user2.getId());
        response = userController.modifyRaters(user.getId(), user2.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        userService.deleteById(user.getId());
        response = userController.modifyRaters(user.getId(), user2.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }
}
