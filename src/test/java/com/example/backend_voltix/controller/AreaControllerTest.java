package com.example.backend_voltix.controller;

import com.example.backend_voltix.dto.Area.AreaDto;
import com.example.backend_voltix.dto.Area.DeleteAreaDto;
import com.example.backend_voltix.model.Area;
import com.example.backend_voltix.service.IAreaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AreaControllerTest {

    @Mock
    private IAreaService areaService;

    @InjectMocks
    private AreaController areaController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(areaController).build();
    }

    @Test
    void testGetAllAreas() throws Exception {
        // Create a list of Area objects using the Builder
        List<Area> areas = Arrays.asList(
                Area.builder()
                        .id(1L)
                        .name("Living Room")
                        .imageUrl("http://example.com/living_room.jpg")
                        .nbrFenetres(2)
                        .nbrPortes(1)
                        .numOfEquipments(3)
                        .numOfPeople(5)
                        .surface(45.0)
                        .build(),
                Area.builder()
                        .id(2L)
                        .name("Bedroom")
                        .imageUrl("http://example.com/bedroom.jpg")
                        .nbrFenetres(1)
                        .nbrPortes(1)
                        .numOfEquipments(2)
                        .numOfPeople(2)
                        .surface(30.0)
                        .build()
        );

        // Mock the service method
        when(areaService.getAllAreas()).thenReturn(areas);

        // Perform the GET request
        MvcResult result = mockMvc.perform(get("/areas/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Log the status
        System.out.println("Status: " + result.getResponse().getStatus());

        // Adjusted expected JSON to only include serialized fields
        String expectedJson = "[{\"id\":1,\"name\":\"Living Room\",\"imageUrl\":\"http://example.com/living_room.jpg\",\"nbrFenetres\":2,\"nbrPortes\":1,\"numOfEquipments\":3,\"numOfPeople\":5,\"surface\":45.0},{\"id\":2,\"name\":\"Bedroom\",\"imageUrl\":\"http://example.com/bedroom.jpg\",\"nbrFenetres\":1,\"nbrPortes\":1,\"numOfEquipments\":2,\"numOfPeople\":2,\"surface\":30.0}]";

        // Check if the response body matches the expected JSON
        mockMvc.perform(get("/areas/getAll"))
                .andExpect(content().json(expectedJson));

        // Log the response content
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    void testAddArea_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "test image content".getBytes());
        AreaDto areaDto = new AreaDto();
        areaDto.setName("Living Room");
        areaDto.setNbrFenetres(2);
        areaDto.setNbrPortes(1);
        areaDto.setSurface(45.0);
        areaDto.setDeviceId(1L); // Provide a valid deviceId

        Area createdArea = new Area();
        createdArea.setId(1L);
        createdArea.setName("Living Room");
        createdArea.setNbrFenetres(2);
        createdArea.setNbrPortes(1);
        createdArea.setSurface(45.0);

        when(areaService.createAreaWithImage(any(MultipartFile.class), any(AreaDto.class))).thenReturn(createdArea);

        // Act
        MvcResult result = mockMvc.perform(multipart("/areas/add")
                        .file(file)
                        .param("name", "Living Room")
                        .param("nbrFenetres", "2")
                        .param("nbrPortes", "1")
                        .param("surface", "45.0")
                        .param("deviceId", "1")) // Add deviceId parameter
                .andReturn();

        // Log the status
        System.out.println("Status: " + result.getResponse().getStatus());

        // Assert
        mockMvc.perform(multipart("/areas/add")
                        .file(file)
                        .param("name", "Living Room")
                        .param("nbrFenetres", "2")
                        .param("nbrPortes", "1")
                        .param("surface", "45.0")
                        .param("deviceId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Living Room"))
                .andExpect(jsonPath("$.nbrFenetres").value(2))
                .andExpect(jsonPath("$.nbrPortes").value(1))
                .andExpect(jsonPath("$.surface").value(45.0));

        // Log the response content
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    void testAddArea_DeviceNotFound() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "test image content".getBytes());

        when(areaService.createAreaWithImage(any(MultipartFile.class), any(AreaDto.class)))
                .thenThrow(new RuntimeException("Device not found"));

        // Act
        MvcResult result = mockMvc.perform(multipart("/areas/add")
                        .file(file)
                        .param("name", "Living Room")
                        .param("nbrFenetres", "2")
                        .param("nbrPortes", "1")
                        .param("surface", "45.0")
                        .param("deviceId", "1")) // Include a deviceId
                .andReturn();

        // Log the status
        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Status: " + status);
        System.out.println("Response: " + responseContent);

        // Assert the status and content
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), status); // Assert the status code
        Assertions.assertEquals("Device not found", responseContent);
    }

    @Test
    void testUpdateAreaSuccess() throws Exception {
        // Arrange
        Long areaId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "test image content".getBytes());

        // Prepare the AreaDto to be returned
        Area updatedArea = new Area();
        updatedArea.setId(areaId);
        updatedArea.setName("Living Room");
        updatedArea.setNbrFenetres(2);
        updatedArea.setNbrPortes(1);
        updatedArea.setSurface(45.0);

        // Prepare the AreaDto that will be sent to the service
        AreaDto areaDto = new AreaDto();
        areaDto.setId(areaId);
        areaDto.setName("Living Room");
        areaDto.setNbrFenetres(2);
        areaDto.setNbrPortes(1);
        areaDto.setSurface(45.0);
        areaDto.setDeviceId(1L);

        // Simulate successful area update
        when(areaService.updateAreaWithImage(eq(areaId), any(MultipartFile.class), eq(areaDto)))
                .thenReturn(updatedArea);

        // Act and Assert
        mockMvc.perform(multipart(HttpMethod.PUT, "/areas/update")
                        .file(file) // Correctly add the file to the request
                        .param("id", String.valueOf(areaId))
                        .param("name", "Living Room")
                        .param("nbrFenetres", "2")
                        .param("nbrPortes", "1")
                        .param("surface", "45.0")
                        .param("deviceId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA)) // Set content type for multipart
                .andExpect(status().isOk()) // Expect a 200 status
                .andExpect(jsonPath("$.id").value(areaId)) // Assert that the response contains the expected area ID
                .andExpect(jsonPath("$.name").value("Living Room")) // Assert the name
                .andExpect(jsonPath("$.nbrFenetres").value(2)) // Assert the number of windows
                .andExpect(jsonPath("$.nbrPortes").value(1)) // Assert the number of doors
                .andExpect(jsonPath("$.surface").value(45.0)) // Assert the surface area
                .andReturn();
    }

    @Test
    void testUpdateArea_NotFound() throws Exception {
        // Arrange
        Long areaId = 1L; // This is the area ID that your mock expects
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "test image content".getBytes());

        // Simulate a runtime exception when the area is not found
        when(areaService.updateAreaWithImage(eq(areaId), any(MultipartFile.class), any(AreaDto.class)))
                .thenThrow(new RuntimeException("Area not found"));

        // Act and Assert
        mockMvc.perform(multipart(HttpMethod.PUT,"/areas/update") // Use multipart to indicate multipart form data
                        .file(file) // Include the file as part of the request
                        .param("id", String.valueOf(areaId)) // ID that will cause the exception
                        .param("name", "Living Room")
                        .param("nbrFenetres", "2")
                        .param("nbrPortes", "1")
                        .param("surface", "45.0")
                        .param("deviceId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA)) // Set content type for multipart
                .andExpect(status().isNotFound()) // Expect a 404 status
                .andExpect(content().string("Area not found")) // Verify the response body
                .andReturn();
    }

    @Test
    void testDeleteAreaSuccess() throws Exception {
        // Arrange
        DeleteAreaDto deleteAreaDto = new DeleteAreaDto();
        deleteAreaDto.setId(1L); // Set the ID of the area to delete

        // Simulate successful deletion
        doNothing().when(areaService).deleteArea(deleteAreaDto.getId());

        // Act and Assert
        mockMvc.perform(delete("/areas/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteAreaDto))) // Convert DTO to JSON
                .andExpect(status().isOk()) // Expect a 200 status
                .andExpect(content().string("Area deleted successfully")); // Check the response content
    }




}
