package org.ispp4.cohabify.tag;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Test
    void shouldFindAllTags() {
        List<Tag> tags = tagService.findAll();
        assertNotNull(tags);
    }

    @Test
    void shouldFindTagById() {
        Tag tag = new Tag();
        tag.setTag("Test Tag");
        tag.setTagType(TagType.USER_TAG);
        tag = tagService.save(tag);
        ObjectId id = tag.getId();

        Optional<Tag> tagOptional = tagService.findById(id);
        assertTrue(tagOptional.isPresent());
    }

    @Test
    void shouldSaveTag() {
        Tag tag = new Tag();
        tag.setTag("Test Tag");
        tag.setTagType(TagType.USER_TAG);

        Tag savedTag = tagService.save(tag);

        assertNotNull(savedTag);
        assertNotNull(savedTag.getId());

        tagService.deleteById(savedTag.getId());
    }

    @Test
    void shouldDeleteTagById() {
        Tag tag = new Tag();
        tag.setTag("Test Tag");
        tag.setTagType(TagType.USER_TAG);
        tag = tagService.save(tag);

        ObjectId id = tag.getId();

        tagService.deleteById(id);

        Optional<Tag> deletedTag = tagService.findById(id);

        assertTrue(deletedTag.isEmpty());
    }
}