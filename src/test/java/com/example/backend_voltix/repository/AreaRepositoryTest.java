package com.example.backend_voltix.repository;

import com.example.backend_voltix.model.Area;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class AreaRepositoryTest {

    @Autowired
    private AreaRepository areaRepository;

    @Test
    public void testSaveArea() {
        Area area = new Area(null, "New Area",null, 1, 2, 5, 3, 50.0, 1, null, null, null, null, null, null, null);
        Area savedArea = areaRepository.save(area);

        assertNotNull(savedArea.getId());
        assertEquals("New Area", savedArea.getName());
    }

    @Test
    @Transactional
    public void testFindById() {
        Area area = new Area(null, "Test Area",null, 2, 3, 10, 5, 100.5, 1, 1L, null, null, null, null, null, null);
        Area savedArea = areaRepository.save(area);

        Optional<Area> foundArea = areaRepository.findById(savedArea.getId());
        assertTrue(foundArea.isPresent());
        assertEquals("Test Area", foundArea.get().getName());
    }

    @Test
    @Transactional
    public void testUpdateArea() {
        Area area = new Area(null, "Area to Update",null, 1, 2, 5, 3, 50.0, 1, null, null, 1L, null, null, null, null);
        Area savedArea = areaRepository.save(area);

        savedArea.setName("Updated Area");
        Area updatedArea = areaRepository.save(savedArea);

        assertEquals("Updated Area", updatedArea.getName());
    }

    @Test
    @Transactional
    public void testDeleteArea() {
        Area area = new Area(null, "Area to Delete",null, 1, 2, 5, 3, 50.0, 1, 100L, null, 1L, null, null, null, null);
        Area savedArea = areaRepository.save(area);

        areaRepository.delete(savedArea);

        Optional<Area> deletedArea = areaRepository.findById(savedArea.getId());
        assertFalse(deletedArea.isPresent());
    }
}
