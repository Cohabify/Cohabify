package org.ispp4.cohabify.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.ispp4.cohabify.house.Heating;
import org.ispp4.cohabify.house.House;
import org.ispp4.cohabify.house.HouseRepository;
import org.ispp4.cohabify.houseAdvertisement.HouseAdvertisement;
import org.ispp4.cohabify.houseAdvertisement.HouseAdvertisementRepository;
import org.ispp4.cohabify.houseRating.HouseRating;
import org.ispp4.cohabify.houseRating.HouseRatingRepository;
import org.ispp4.cohabify.tag.Tag;
import org.ispp4.cohabify.tag.TagRepository;
import org.ispp4.cohabify.user.User;
import org.ispp4.cohabify.user.UserRepository;
import org.ispp4.cohabify.userAdvertisement.UserAdvertisement;
import org.ispp4.cohabify.userAdvertisement.UserAdvertisementRepository;
import org.ispp4.cohabify.userRating.UserRating;
import org.ispp4.cohabify.userRating.UserRatingRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
@Profile("!prod")
public class DataLoader implements ApplicationRunner {

    private UserRepository userRepository;
    private TagRepository tagRepository;
    private HouseRepository houseRepository;
    private HouseAdvertisementRepository houseAdvertisementRepository;
    private UserAdvertisementRepository userAdvertisementRepository;
    private UserRatingRepository userRatingRepository;
    private HouseRatingRepository houseRatingRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Carga el archivo JSON como un recurso
        Resource resource = new ClassPathResource("data/initial_data.json");

        // Lee los datos del archivo JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

        userRepository.deleteAll();
        tagRepository.deleteAll();
        houseRepository.deleteAll();
        houseAdvertisementRepository.deleteAll();
        userAdvertisementRepository.deleteAll();
        userRatingRepository.deleteAll();

        // Procesa las etiquetas
        JsonNode tagsNode = rootNode.get("tags");
        if (tagsNode != null) {
            List<Tag> tagsToInsert = Arrays.asList(objectMapper.readValue(tagsNode.toString(), Tag[].class));
            tagRepository.saveAll(tagsToInsert);
            System.out.println(tagsToInsert.size() + " etiquetas insertadas correctamente.");
        }

        // Procesa los usuarios
        JsonNode usersNode = rootNode.get("users");
        if (usersNode != null) {
            List<User> usersToInsert = Arrays.asList(objectMapper.readValue(usersNode.toString(), User[].class));
            userRepository.saveAll(usersToInsert);
            System.out.println(usersToInsert.size() + " usuarios insertados correctamente.");
        }

        // Procesa las valoraciones de usuarios
        JsonNode userRatingNode = rootNode.get("userRatings");
        if (tagsNode != null) {
            List<UserRating> ratingsToInsert = Arrays
                    .asList(objectMapper.readValue(userRatingNode.toString(), UserRating[].class));
            userRatingRepository.saveAll(ratingsToInsert);
            System.out.println(ratingsToInsert.size() + " valoraciones insertadas correctamente.");
        }

        // Procesa las viviendas
        JsonNode housesNode = rootNode.get("houses");
        if (housesNode != null) {
            List<House> housesToInsert = new ArrayList<>();
            for (JsonNode houseNode : housesNode) {
                House house = new House();

                if (houseNode.has("id")) {
                    ObjectId id = new ObjectId(houseNode.get("id").asText());
                    house.setId(id);
                }

                // Parsea los datos del json del locationPoint en un objeto de tipo GeoJsonPoint
                if (houseNode.has("locationPoint")) {
                    JsonNode locationPointString = houseNode.get("locationPoint");
                    Double x = locationPointString.get("x").asDouble();
                    Double y = locationPointString.get("y").asDouble();
                    house.setLocationPoint(new GeoJsonPoint(x, y));
                }

                // Parsea los datos del json de un objeto con DBRef
                if (houseNode.has("tags")) {
                    List<Tag> tags = Arrays
                            .asList(objectMapper.readValue(houseNode.get("tags").toString(), Tag[].class));
                    house.setTags(tags);
                }

                if (houseNode.has("roomsNumber")) {
                    house.setRoomsNumber(houseNode.get("roomsNumber").asInt());
                }

                if (houseNode.has("bathroomsNumber")) {
                    house.setBathroomsNumber(houseNode.get("bathroomsNumber").asInt());
                }

                if (houseNode.has("area")) {
                    house.setArea(houseNode.get("area").asInt());
                }

                if (houseNode.has("heating")) {
                    house.setHeating(Heating.valueOf(houseNode.get("heating").asText()));
                }

                if (houseNode.has("floor")) {
                    house.setFloor(houseNode.get("floor").asInt());
                }

                if (houseNode.has("location")) {
                    house.setLocation(houseNode.get("location").asText());
                }

                if (houseNode.has("cadastre")) {
                    house.setCadastre(houseNode.get("cadastre").asText());
                }
                housesToInsert.add(house);
            }
            houseRepository.saveAll(housesToInsert);
            System.out.println(housesToInsert.size() + " viviendas insertadas correctamente.");
        }

        // Procesa los anuncios de viviendas
        JsonNode houseAdvertisementsNode = rootNode.get("houseAdvertisements");
        if (houseAdvertisementsNode != null) {
            List<HouseAdvertisement> houseAdvertisementsToInsert = Arrays
                    .asList(objectMapper.readValue(houseAdvertisementsNode.toString(), HouseAdvertisement[].class));
            houseAdvertisementRepository.saveAll(houseAdvertisementsToInsert);
            System.out.println(houseAdvertisementsToInsert.size() + " anuncios de viviendas insertados correctamente.");
        }

        // Procesa los anuncios de usuarios
        JsonNode userAdvertismentsNode = rootNode.get("userAdvertisements");
        if (userAdvertismentsNode != null) {
            List<UserAdvertisement> userAdvertisementsToInsert = Arrays
                    .asList(objectMapper.readValue(userAdvertismentsNode.toString(), UserAdvertisement[].class));
            userAdvertisementRepository.saveAll(userAdvertisementsToInsert);
            System.out.println(userAdvertisementsToInsert.size() + " anuncios de usuario insertados correctamente.");
        }
        // Procesa las valoraciones de usuarios
        JsonNode houseRatingNode = rootNode.get("houseRatings");
        if (tagsNode != null) {
            List<HouseRating> ratingsToInsert = Arrays
                    .asList(objectMapper.readValue(houseRatingNode.toString(), HouseRating[].class));
            houseRatingRepository.saveAll(ratingsToInsert);
            System.out.println(ratingsToInsert.size() + " valoraciones insertadas correctamente.");
        }
    }

}
