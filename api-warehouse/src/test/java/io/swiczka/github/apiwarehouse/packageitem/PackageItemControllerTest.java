package io.swiczka.github.apiwarehouse.packageitem;

import io.swiczka.github.apiwarehouse.enums.PackageStatus;
import io.swiczka.github.apiwarehouse.exceptions.ForbiddenException;
import io.swiczka.github.apiwarehouse.packageitem.response.PackageItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PackageItemController.class)
class PackageItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PackageItemService packageItemService;

    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Nested
    @DisplayName("GET /api/package/inbound/{inboundOrderId}")
    class GetPackagesByInboundOrderId {

        @Test
        @DisplayName("Should return 200 and list of packages")
        void getPackagesByInboundOrderId_ok() throws Exception {
            // given
            final Long inboundOrderId = 10L;
            final PackageItemResponse packageResponse = new PackageItemResponse(
                    1L,
                    "Package1",
                    inboundOrderId,
                    null,
                    2,
                    5,
                    PackageStatus.ALLOCATED,
                    20L
            );

            when(packageItemService.getPackagesByInboundOrderId(inboundOrderId, TEST_USER_ID))
                    .thenReturn(List.of(packageResponse));

            // when / then
            mockMvc.perform(get("/api/package/inbound/{inboundOrderId}", inboundOrderId)
                            .header("X-Guest-Id", TEST_USER_ID.toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Package1"))
                    .andExpect(jsonPath("$[0].inboundOrderId").value(inboundOrderId))
                    .andExpect(jsonPath("$[0].status").value("ALLOCATED"))
                    .andExpect(jsonPath("$[0].layoutId").value(20));
        }

        @Test
        @DisplayName("Should return 403 Forbidden when user is not authorized")
        void getPackagesByInboundOrderId_forbidden() throws Exception {
            // given
            final Long inboundOrderId = 10L;
            when(packageItemService.getPackagesByInboundOrderId(inboundOrderId, TEST_USER_ID))
                    .thenThrow(new ForbiddenException("Access denied to packages for order with id " + inboundOrderId));

            // when / then
            mockMvc.perform(get("/api/package/inbound/{inboundOrderId}", inboundOrderId)
                            .header("X-Guest-Id", TEST_USER_ID.toString()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 Bad Request when X-Guest-Id header is missing")
        void getPackagesByInboundOrderId_noGuestIdHeader() throws Exception {
            // given
            final Long inboundOrderId = 10L;

            // when / then
            mockMvc.perform(get("/api/package/inbound/{inboundOrderId}", inboundOrderId))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 200 and empty array when no packages exist for order")
        void getPackagesByInboundOrderId_empty() throws Exception {
            // given
            final Long inboundOrderId = 999L;
            when(packageItemService.getPackagesByInboundOrderId(inboundOrderId, TEST_USER_ID))
                    .thenReturn(List.of());

            // when / then
            mockMvc.perform(get("/api/package/inbound/{inboundOrderId}", inboundOrderId)
                            .header("X-Guest-Id", TEST_USER_ID.toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }
}
