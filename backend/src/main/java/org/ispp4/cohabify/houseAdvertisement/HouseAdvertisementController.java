package org.ispp4.cohabify.houseAdvertisement;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.ispp4.cohabify.utils.Global;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.ispp4.cohabify.dto.AdvertisementHouseRequest;
import org.ispp4.cohabify.dto.FormItemValidationError;
import org.ispp4.cohabify.house.Heating;
import org.ispp4.cohabify.house.House;
import org.ispp4.cohabify.house.HouseService;
import org.ispp4.cohabify.storage.StorageService;
import org.ispp4.cohabify.user.Plan;
import org.ispp4.cohabify.user.User;
import org.ispp4.cohabify.user.UserService;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import java.util.UUID;

@RestController
@RequestMapping("/api/advertisements/houses")
@AllArgsConstructor
public class HouseAdvertisementController {
    
    private HouseAdvertisementService advertisementService;
    private HouseService houseService;
    private StorageService storageService;
    private UserService userService;
    private Global global;

    @Transactional(readOnly = true)
    @GetMapping("")
    public ResponseEntity<List<HouseAdvertisement>> getAllAdvertisements(@Nullable Principal principal) {
        List<HouseAdvertisement> advertisements = advertisementService.findAll();

        if (principal == null) {
            advertisements = advertisements.stream() 
            // Filter advertisements to leave the ones that are owned or that were created at least a day before now
        .filter(a -> System.currentTimeMillis() > (a.getId().getTimestamp() & 0xFFFFFFFFL) * 1000L + 86400000).toList();
        }else{
            User user = userService.getUserByUsername(principal.getName());
            if(user.getPlan().equals(Plan.BASIC) ) {

                advertisements = advertisements.stream() 
                                                // Filter advertisements to leave the ones that are owned or that were created at least a day before now
                                            .filter(a -> a.getAuthor().getId().equals(user.getId()) ||
                                                            System.currentTimeMillis() > (a.getId().getTimestamp() & 0xFFFFFFFFL) * 1000L + 86400000)
                                            .toList();
            }
        } 
        
        return new ResponseEntity<>(advertisements, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<HouseAdvertisement> getAdvertisement(@PathVariable String id) {
        Optional<HouseAdvertisement> advertisement = advertisementService.findById(new ObjectId(id));
        if(global.getCurrentUser() == null || 
            !advertisement.get().getAuthor().getUsername().equals(global.getCurrentUser().getUsername())){
            advertisement.get().setViews(advertisement.get().getViews()+1);
            advertisementService.update(advertisement.get().getId(), advertisement.get());
        }
        if(advertisement.isPresent()){
            return new ResponseEntity<>(advertisement.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/owner/{id}")
    public ResponseEntity<List<HouseAdvertisement>> getAdvertisementsByAuthor(@PathVariable String id) {
        List<HouseAdvertisement> advertisements = advertisementService.findByAuthorId(new ObjectId(id));
        return new ResponseEntity<>(advertisements, HttpStatus.OK);
    }

    @PostMapping("")
	public ResponseEntity<?> createAdvertisement(@Valid @RequestPart("string-data") AdvertisementHouseRequest request, BindingResult result, 
    @RequestPart(value = "images",required = true) List<MultipartFile> images) throws BadRequestException {

        if (images == null || images.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "images are required");
        }

        if(request.getAuthor().getUsername().equals(global.getCurrentUser().getUsername())){
            House house = new House();
            house.setRoomsNumber(request.getHouse().getRoomsNumber());
            house.setBathroomsNumber(request.getHouse().getBathroomsNumber());
            house.setArea(request.getHouse().getArea());
            house.setFloor(request.getHouse().getFloor());
            house.setLocation(request.getHouse().getLocation());
            house.setCadastre(request.getHouse().getCadastre());
            house.setHeating(request.getHouse().getHeating());
            house.setTags(request.getHouse().getTags());
            GeoJsonPoint point = new GeoJsonPoint(2, 2);
            house.setLocationPoint(point);
            house = houseService.save(house);
            

            HouseAdvertisement advertisement = new HouseAdvertisement();
            advertisement.setViews(0);
            advertisement.setTitle(request.getTitle());
            advertisement.setDescription(request.getDescription());
            advertisement.setPrice(request.getPrice());
            advertisement.setTenants(request.getTenants());
            advertisement.setHouse(house);
            advertisement.setAuthor(request.getAuthor());
            advertisement = advertisementService.save(advertisement);
            
            // Save the image and add the static uri to the user
            
            List<String> imagesPath = new ArrayList<>();
            for(int i = 0; i < images.size(); i++){
                MultipartFile image = images.get(i);
                String[] filename_split = images.get(i).getOriginalFilename().split("\\.");
                String filename = advertisement.getJsonId() +UUID.randomUUID().toString() + "." + filename_split[filename_split.length-1];
                String static_path;
                try {
                    static_path = storageService.saveImage(filename, image);
                } catch (IOException e) {
                    advertisementService.deleteById(advertisement.getId());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(e.getMessage());
                }
            
                imagesPath.add(static_path);
            }
            advertisement.setImages(imagesPath);
            advertisement = advertisementService.save(advertisement);
            return ResponseEntity.status(HttpStatus.CREATED)
							 .body(advertisement);  
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("You can't create an advertisement for yourself");
        } 
       

    }
		
    


    @GetMapping("/heating")
    public ResponseEntity<List<Heating>> findHeating() {
        try {
            List<Heating> heatings = List.of(Heating.values());

            if (heatings.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(heatings, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*@PutMapping("/{id}")
    public ResponseEntity<HouseAdvertisement> updateAdvertisement(@PathVariable ObjectId id, @RequestBody HouseAdvertisement advertisement) {
        HouseAdvertisement updatedAdvertisement = advertisementService.update(id, advertisement);
        return new ResponseEntity<>(updatedAdvertisement, HttpStatus.OK);
    }*/

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdvertisement(@Valid @RequestPart("string-data") AdvertisementHouseRequest request, BindingResult result, 
    @RequestPart(value = "images",required = true) List<MultipartFile> images,@PathVariable ObjectId id) throws BadRequestException {
		
		if(result.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								 .body(result.getFieldErrors()
										 	 .stream()
										 	 	.map(fe -> new FormItemValidationError(fe))
										 	 	.toList());
		}

        if (images == null || images.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "images are required");
        }

        if(request.getAuthor().getUsername().equals(global.getCurrentUser().getUsername())){
            
            House house = houseService.findById(request.getHouseId()).get();
            house.setRoomsNumber(request.getHouse().getRoomsNumber());
            house.setBathroomsNumber(request.getHouse().getBathroomsNumber());
            house.setArea(request.getHouse().getArea());
            house.setFloor(request.getHouse().getFloor());
            house.setLocation(request.getHouse().getLocation());
            house.setCadastre(request.getHouse().getCadastre());
            house.setHeating(request.getHouse().getHeating());
            house.setTags(request.getHouse().getTags());
            GeoJsonPoint point = new GeoJsonPoint(2, 2);
            house.setLocationPoint(point);
            house = houseService.save(house);

            HouseAdvertisement advertisement = advertisementService.findAdById(id);
            
            advertisement.setViews(request.getViews());
            advertisement.setTitle(request.getTitle());
            advertisement.setDescription(request.getDescription());
            advertisement.setPrice(request.getPrice());
            advertisement.setTenants(request.getTenants());
            advertisement.setHouse(house);
            advertisement.setAuthor(request.getAuthor());
            advertisement = advertisementService.save(advertisement);
            
            // Save the image and add the static uri to the user
            
            List<String> imagesPath = request.getImagesB();
            if(images != null){
                
                for(int i = 0; i < images.size(); i++){
                    MultipartFile image = images.get(i);
                    String[] filename_split = images.get(i).getOriginalFilename().split("\\.");
                    String filename = advertisement.getJsonId() +UUID.randomUUID().toString() + "." + filename_split[filename_split.length-1];
                    String static_path;
                    try {
                        static_path = storageService.saveImage(filename, image);
                    } catch (IOException e) {
                        advertisementService.deleteById(advertisement.getId());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(e.getMessage());
                    }
                
                    imagesPath.add(static_path);
                    
                }
                advertisement.setImages(imagesPath);
                advertisement = advertisementService.save(advertisement);
                
            }else{
                imagesPath = request.getImagesB();
                advertisement.setImages(imagesPath);
                advertisement = advertisementService.save(advertisement);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED)
                                .body(advertisement);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("You can't update an advertisement for yourself");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable ObjectId id) {
        if(!advertisementService.findAdById(id).getAuthor().getUsername().equals(global.getCurrentUser().getUsername())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }else{
            advertisementService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }
}