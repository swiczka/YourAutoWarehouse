package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.exceptions.LayoutNotFoundException;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.sharedcommon.helpers.Direction;
import io.swiczka.github.apiwarehouse.layout.response.LayoutReadResponse;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WarehouseLayoutController.class)
class WarehouseLayoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private WarehouseLayoutService layoutService;

    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private LayoutReadResponse createTestResponse() {
        GridDataDto gridDataDto = new GridDataDto(
                Set.of(Direction.UP),
                new Coordinate(0, 1),
                false,
                true
        );

        return new LayoutReadResponse(
                1L,
                TEST_USER_ID,
                Instant.parse("2026-08-20T10:00:00Z"),
                5,
                List.of(gridDataDto)
        );
    }

    @BeforeEach
    void setUp() {
    }

    @Nested
    @DisplayName("POST /api/warehouse/layout")
    class SetNewLayout {

        @Test
        @DisplayName("should return 200 and layout response")
        void setNewLayout_ok() throws Exception{
            // given
            LayoutReadResponse expectedResponse = createTestResponse();
            List<GridDataDto> requestBody = List.of(
                    new GridDataDto(Set.of(Direction.UP), new Coordinate(0, 1), false, true)
            );

            when(layoutService.saveNewLayout(requestBody, TEST_USER_ID)).thenReturn(expectedResponse);

            // when/then
            mockMvc.perform(post("/api/warehouse/layout")
                            .header("X-Guest-Id", TEST_USER_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.forkliftNumber").value(5))
                    .andExpect(jsonPath("$.gridData").isArray())
                    .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()))
                    .andExpect(jsonPath("$.gridData.length()").value(1));
        }
    }

    @Test
    @DisplayName("should ask for X-Guest-Id header")
    void setNewLayout_noGuestIdHeader() throws Exception {
        // given
        List<GridDataDto> requestBody = List.of(
                new GridDataDto(Set.of(Direction.UP), new Coordinate(0, 1), false, true)
        );

        // when/then
        mockMvc.perform(post("/api/warehouse/layout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Nested
    @DisplayName("getUserLayout")
    class GetUserLayout {
        @Test
        @DisplayName("Should return user's newest layout")
        void getUserLayout_ok() throws Exception {
            // given
            LayoutReadResponse expectedResponse = createTestResponse();

            //when
            when(layoutService.getUserLayout(TEST_USER_ID)).thenReturn(expectedResponse);

            //then
            mockMvc.perform(get("/api/warehouse/layout")
                            .header("X-Guest-Id", TEST_USER_ID.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()));

        }

        @Test
        @DisplayName("Should throw not found when given user has no layouts")
        void getUserLayout_layoutNotFound() throws Exception {
            // given

            //when
            when(layoutService.getUserLayout(TEST_USER_ID)).thenThrow(LayoutNotFoundException.class);

            //then
            mockMvc.perform(get("/api/warehouse/layout")
                            .header("X-Guest-Id", TEST_USER_ID.toString()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should throw bad request when there is no Guest Id header")
        void getUserLayout_noGuestIdHeader() throws Exception {
            // given

            //when then
            mockMvc.perform(get("/api/warehouse/layout"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("setForkliftNumber")
    class SetForkliftNumber {
        @Test
        @DisplayName("Should update forklift number for warehouse with given Id")
        void setForkliftNumber_ok() throws Exception{
            //given
            LayoutReadResponse expectedResponse = createTestResponse();
            Integer newForklift = 5;

            //when
            when(layoutService.updateForkliftNumber(expectedResponse.id(), newForklift))
                    .thenReturn(expectedResponse);

            //then
            mockMvc.perform(patch("/api/warehouse/1/forklift")
                            .param("forkliftNumber", newForklift.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(expectedResponse.id()))
                    .andExpect(jsonPath("$.forkliftNumber").value(newForklift));
        }

        @DisplayName("Should throw bad request when forklift number is out of bounds")
        @ParameterizedTest()
        @ValueSource(ints = {-2, 0, 99})
        void setForkliftNumber_badForkliftNumberValue(final int badValue) throws Exception{
            //given

            //when then
            mockMvc.perform(patch("/api/warehouse/1/forklift")
                            .param("forkliftNumber", String.valueOf(badValue)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should throw not found when no such layout exists")
        void setForkliftNumber_layoutNotFound() throws Exception{
            //given

            //when
            when(layoutService.updateForkliftNumber(1L, 5))
                    .thenThrow(LayoutNotFoundException.class);

            //then
            mockMvc.perform(patch("/api/warehouse/1/forklift")
                            .param("forkliftNumber", "5"))
                    .andExpect(status().isNotFound());
        }

    }
}