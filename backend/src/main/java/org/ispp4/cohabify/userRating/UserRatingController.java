package org.ispp4.cohabify.userRating;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/userRating")
@AllArgsConstructor
public class UserRatingController {
    
    @Autowired
    UserRatingService userRatingService;

    @GetMapping("")
    public ResponseEntity<List<UserRating>> getAllUserRatings() {
        try	{
            List<UserRating> userRatings = userRatingService.findAll();
                return ResponseEntity.ok(userRatings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ratedUser/{ratedUserId}")
    public ResponseEntity<List<UserRating>> getAllUserRatingsByRatedUserId(@PathVariable("ratedUserId") ObjectId ratedUserId) {
        try	{
            List<UserRating> userRatings = userRatingService.findByRatedUserId(ratedUserId);
            return ResponseEntity.ok(userRatings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserRating>> getAllUserRatingsByUserId(@PathVariable("userId") ObjectId userId) {
        try	{
            List<UserRating> userRatings = userRatingService.findByUserId(userId);
            return ResponseEntity.ok(userRatings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{userRatingId}")
    public ResponseEntity<UserRating> getUserRatingById(@PathVariable("userRatingId") ObjectId userRatingId) {
        try	{
            UserRating userRating = userRatingService.findById(userRatingId);
            if (userRating != null) {
                return ResponseEntity.ok(userRating);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/ratedUser/{ratedUserId}")
    public ResponseEntity<Void> deleteUserRatingsByRatedUserId(@PathVariable("ratedUserId") ObjectId ratedUserId) {
        try	{
            userRatingService.deleteByRatedUserId(ratedUserId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUserRatingsByUserId(@PathVariable("userId") ObjectId userId) {
        try	{
            userRatingService.deleteByUserId(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{userRatingId}")
    public ResponseEntity<Void> deleteUserRatingById(@PathVariable("userRatingId") ObjectId userRatingId) {
        try	{
            userRatingService.deleteById(userRatingId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<UserRating> createUserRating(@RequestBody @Valid UserRating userRating) {
        try	{
            UserRating newUserRating = userRatingService.save(userRating);
            return ResponseEntity.ok(newUserRating);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("")
    public ResponseEntity<UserRating> updateUserRating(@Valid @RequestBody UserRating userRating) {
        try	{
            UserRating updatedUserRating = userRatingService.save(userRating);
            return ResponseEntity.ok(updatedUserRating);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }   
}