package io.swiczka.github.apiorder;

import io.swiczka.github.apiorder.dto.InboundOrderCreateDto;
import io.swiczka.github.apiorder.dto.InboundOrderReadDto;
import io.swiczka.github.apiorder.enums.InboundOrderStatus;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private OrderService orderService;

    private static final UUID TEST_OPERATOR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long TEST_COMPANY_ID = 42L;
    private static final Long TEST_ORDER_ID = 1L;

    private InboundOrderCreateDto createValidDto() {
        return new InboundOrderCreateDto(
                TEST_OPERATOR_ID,
                TEST_COMPANY_ID,
                List.of("Package-A", "Package-B")
        );
    }

    private InboundOrderResponse createTestResponse() {
        return new InboundOrderResponse(
                TEST_ORDER_ID,
                TEST_OPERATOR_ID,
                TEST_COMPANY_ID,
                2
        );
    }

    @Nested
    @DisplayName("POST /api/order/inbound")
    class AddNewInboundOrder {

        @Test
        @DisplayName("Should return 201 with location header and response body")
        void addNewInboundOrder_ok() throws Exception {
            // given
            InboundOrderCreateDto requestBody = createValidDto();
            InboundOrderResponse expectedResponse = createTestResponse();

            when(orderService.addInboundOrder(any(InboundOrderCreateDto.class))).thenReturn(expectedResponse);

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/order/inbound/" + TEST_ORDER_ID))
                    .andExpect(jsonPath("$.id").value(TEST_ORDER_ID))
                    .andExpect(jsonPath("$.operatorId").value(TEST_OPERATOR_ID.toString()))
                    .andExpect(jsonPath("$.companyId").value(TEST_COMPANY_ID))
                    .andExpect(jsonPath("$.packageCount").value(2));
        }

        @Test
        @DisplayName("Should return 400 when operatorId is null")
        void addNewInboundOrder_nullOperatorId() throws Exception {
            // given
            InboundOrderCreateDto requestBody = new InboundOrderCreateDto(
                    null,
                    TEST_COMPANY_ID,
                    List.of("Package-A")
            );

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when companyId is null")
        void addNewInboundOrder_nullCompanyId() throws Exception {
            // given
            InboundOrderCreateDto requestBody = new InboundOrderCreateDto(
                    TEST_OPERATOR_ID,
                    null,
                    List.of("Package-A")
            );

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when packageNames list is empty")
        void addNewInboundOrder_emptyPackageList() throws Exception {
            // given
            InboundOrderCreateDto requestBody = new InboundOrderCreateDto(
                    TEST_OPERATOR_ID,
                    TEST_COMPANY_ID,
                    List.of()
            );

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when packageNames list exceeds 15 items")
        void addNewInboundOrder_tooManyPackages() throws Exception {
            // given
            List<String> tooManyPackages = List.of(
                    "P1", "P2", "P3", "P4", "P5",
                    "P6", "P7", "P8", "P9", "P10",
                    "P11", "P12", "P13", "P14", "P15", "P16"
            );
            InboundOrderCreateDto requestBody = new InboundOrderCreateDto(
                    TEST_OPERATOR_ID,
                    TEST_COMPANY_ID,
                    tooManyPackages
            );

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when request body is missing")
        void addNewInboundOrder_missingBody() throws Exception {
            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/order/inbound")
    class GetInboundByUser {

        @Test
        @DisplayName("Should return 200 with list of inbound orders for given userId")
        void getInboundByUser_ok() throws Exception {
            // given
            List<InboundOrderReadDto> expectedOrders = List.of(
                    new InboundOrderReadDto(
                            TEST_ORDER_ID,
                            TEST_OPERATOR_ID,
                            Instant.parse("2026-08-20T10:00:00Z"),
                            TEST_COMPANY_ID,
                            InboundOrderStatus.PENDING
                    )
            );

            when(orderService.getInboundByUser(TEST_OPERATOR_ID)).thenReturn(expectedOrders);

            // when/then
            mockMvc.perform(get("/api/order/inbound")
                            .param("userId", TEST_OPERATOR_ID.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(TEST_ORDER_ID))
                    .andExpect(jsonPath("$[0].operatorId").value(TEST_OPERATOR_ID.toString()))
                    .andExpect(jsonPath("$[0].companyId").value(TEST_COMPANY_ID))
                    .andExpect(jsonPath("$[0].status").value("PENDING"));
        }

        @Test
        @DisplayName("Should return 200 with empty list when user has no orders")
        void getInboundByUser_emptyList() throws Exception {
            // given
            when(orderService.getInboundByUser(TEST_OPERATOR_ID)).thenReturn(List.of());

            // when/then
            mockMvc.perform(get("/api/order/inbound")
                            .param("userId", TEST_OPERATOR_ID.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 400 when userId param is missing")
        void getInboundByUser_missingUserId() throws Exception {
            // when/then
            mockMvc.perform(get("/api/order/inbound"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when userId is not a valid UUID")
        void getInboundByUser_invalidUserId() throws Exception {
            // when/then
            mockMvc.perform(get("/api/order/inbound")
                            .param("userId", "not-a-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }
}
