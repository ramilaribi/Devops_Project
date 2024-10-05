package com.example.backend_voltix.service;

import com.example.backend_voltix.dto.Area.AreaDto;
import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.model.Device;
import com.example.backend_voltix.repository.AreaRepository;
import com.example.backend_voltix.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class AreaServiceTest {

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private AreaService areaService;

    private String uploadPath;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Dynamically set the upload path in AreaService using Reflection
        uploadPath = "src/test/resources/static/uploads/";
        ReflectionTestUtils.setField(areaService, "uploadPath", uploadPath);
    }

    private MultipartFile createMockMultipartFile(String filename, String contentType, String content) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return filename;
            }

            @Override
            public String getOriginalFilename() {
                return filename;
            }

            @Override
            public long getSize() {
                return content.length();
            }

            @Override
            public boolean isEmpty() {
                return content.isEmpty();
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(content.getBytes());
            }

            @Override
            public byte[] getBytes() {
                return content.getBytes();
            }

            @Override
            public void transferTo(java.io.File dest) {
                // Simulate file transfer
                try (InputStream inputStream = getInputStream()) {
                    java.nio.file.Files.copy(inputStream, dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to transfer file", e);
                }
            }
        };
    }
    @Test
    void testGetAllAreas() {
        Area area1 = new Area();
        Area area2 = new Area();
        when(areaRepository.findAll()).thenReturn(List.of(area1, area2));
        List<Area> areas = areaService.getAllAreas();
        assertEquals(2, areas.size());
        verify(areaRepository, times(1)).findAll();
    }
    @Test
    void testDeleteArea() {
        Long areaId = 1L;
        Area existingArea = new Area();
        existingArea.setId(areaId);
        when(areaRepository.findById(areaId)).thenReturn(Optional.of(existingArea));
        areaService.deleteArea(areaId);
        verify(areaRepository, times(1)).findById(areaId);
        verify(areaRepository, times(1)).delete(existingArea);
    }
    @Test
    void testCreateAreaWithImage() throws Exception {
        MultipartFile mockFile = createMockMultipartFile("testImage.jpg", "image/jpeg", "test data");

        AreaDto areaDto = new AreaDto();
        areaDto.setName("New Area");
        areaDto.setNbrFenetres(4);
        areaDto.setNbrPortes(2);
        areaDto.setSurface(100.0);

        Area savedArea = new Area();
        savedArea.setName("New Area");
        savedArea.setImageUrl(uploadPath + "testImage.jpg");

        when(deviceRepository.findById(anyLong())).thenReturn(Optional.of(new Device()));
        when(areaRepository.save(any(Area.class))).thenReturn(savedArea);

        Area createdArea = areaService.createAreaWithImage(mockFile, areaDto);

        assertNotNull(createdArea);
        assertEquals("New Area", createdArea.getName());
        assertEquals(uploadPath + "testImage.jpg", createdArea.getImageUrl());
        verify(areaRepository, times(1)).save(any(Area.class));
    }

    @Test
    void testCreateAreaWithImage_FileIsEmpty() throws Exception {
        MultipartFile emptyFile = new MultipartFile() {
            @Override
            public String getName() {
                return "emptyFile";
            }

            @Override
            public String getOriginalFilename() {
                return "emptyImage.jpg";
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public String getContentType() {
                return "image/jpeg";
            }

            @Override
            public InputStream getInputStream() {
                throw new UnsupportedOperationException("Not needed for this test");
            }

            @Override
            public byte[] getBytes() {
                return new byte[0];
            }

            @Override
            public void transferTo(java.io.File dest) {
                throw new UnsupportedOperationException("Not needed for this test");
            }
        };

        AreaDto areaDto = new AreaDto();
        areaDto.setName("Area Without Image");
        areaDto.setNbrFenetres(3);
        areaDto.setNbrPortes(1);
        areaDto.setSurface(50.0);

        Area savedArea = new Area();
        savedArea.setName("Area Without Image");
        savedArea.setImageUrl(null);  // No image URL since the file is empty

        when(deviceRepository.findById(anyLong())).thenReturn(Optional.of(new Device()));
        when(areaRepository.save(any(Area.class))).thenReturn(savedArea);

        Area createdArea = areaService.createAreaWithImage(emptyFile, areaDto);

        assertNotNull(createdArea);
        assertEquals("Area Without Image", createdArea.getName());
        assertNull(createdArea.getImageUrl());
        verify(areaRepository, times(1)).save(any(Area.class));
    }

    @Test
     void testUpdateAreaWithImage_Success() throws IOException {
        // Arrange
        Long areaId = 1L;
        Long deviceId = 1L;

        // Create a mock MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("testImage.jpg");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        when(mockFile.isEmpty()).thenReturn(false);

        // Set up existing area
        Area existingArea = new Area();
        existingArea.setId(areaId);
        existingArea.setImageUrl("/oldImage.jpg");
        existingArea.setName("Old Area");
        existingArea.setNbrFenetres(3);
        existingArea.setNbrPortes(1);
        existingArea.setSurface(80.0);

        // Set up mock device
        Device mockDevice = new Device();
        mockDevice.setId(deviceId);
        // Set other properties of mockDevice as necessary

        // Set up area DTO with a valid device ID
        AreaDto areaDto = new AreaDto();
        areaDto.setName("Updated Area");
        areaDto.setNbrFenetres(5);
        areaDto.setNbrPortes(2);
        areaDto.setSurface(100.0);
        areaDto.setDeviceId(deviceId); // Set the device ID

        // Mock repository behavior
        when(areaRepository.findById(areaId)).thenReturn(Optional.of(existingArea));
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(mockDevice)); // Mock device retrieval
        when(areaRepository.save(any(Area.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return the updated area

        // Act
        Area updatedArea = areaService.updateAreaWithImage(areaId, mockFile, areaDto);

        // Assert
        assertNotNull(updatedArea, "Updated area should not be null");
        assertEquals("Updated Area", updatedArea.getName());
        assertEquals(5, updatedArea.getNbrFenetres());
        assertEquals(2, updatedArea.getNbrPortes());
        assertEquals(100.0, updatedArea.getSurface());

        // Verify that the image URL was set with the filename
        String expectedFileName = "testImage.jpg";
        assertTrue(updatedArea.getImageUrl().contains(expectedFileName));

        // Verify that the repository save method was called
        verify(areaRepository, times(1)).save(any(Area.class)); // Check that save was called exactly once

    }




}
