package org.ispp4.cohabify.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class TagControllerTest {

    @Autowired
    private TagController tagController;

    @Autowired
    private TagRepository tagRepository;

    @Test
    public void testCreateTag() {
        Tag newTag = new Tag("Test Tag", TagType.USER_TAG);
        ResponseEntity<Tag> response = tagController.createTag(newTag);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        tagRepository.delete(response.getBody());
    }

    @Test
    public void testGetAllTags() {
        
        tagRepository.save(new Tag("Tag1", TagType.USER_TAG));
        tagRepository.save(new Tag("Tag2", TagType.USER_TAG));

        ResponseEntity<List<Tag>> response = tagController.getAllTags(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetTagById() {
        
        Tag savedTag = tagRepository.save(new Tag("Test Tag", TagType.USER_TAG));

        ResponseEntity<Tag> response = tagController.getTagById(savedTag.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        
        tagRepository.delete(savedTag);
    }

    @Test
    public void testFindByTagType() {
        
        tagRepository.save(new Tag("Tag1", TagType.USER_TAG));
        tagRepository.save(new Tag("Tag2", TagType.FLAT_TAG));

        ResponseEntity<List<Tag>> response = tagController.findByTagType(TagType.FLAT_TAG);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testUpdateTag() {
        
        Tag savedTag = tagRepository.save(new Tag("Test Tag", TagType.USER_TAG));

        
        savedTag.setTag("Updated Tag");

        ResponseEntity<Tag> response = tagController.updateTag(savedTag.getId(), savedTag);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        
        Tag updatedTag = tagRepository.findById(savedTag.getId()).orElse(null);
        assertNotNull(updatedTag);
        assertEquals(savedTag.getTag(), updatedTag.getTag());

        
        tagRepository.delete(updatedTag);
    }

    @Test
    public void testDeleteTag() {
        
        Tag savedTag = tagRepository.save(new Tag("Test Tag", TagType.USER_TAG));

        ResponseEntity<HttpStatus> response = tagController.deleteTag(savedTag.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        
        assertFalse(tagRepository.existsById(savedTag.getId()));
    }
}