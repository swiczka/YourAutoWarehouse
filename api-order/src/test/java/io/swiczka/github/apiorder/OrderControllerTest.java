package io.swiczka.github.apiorder;

import io.swiczka.github.apiorder.dto.InboundOrderCreateDto;
import io.swiczka.github.apiorder.dto.OutboundOrderCreateDto;
import io.swiczka.github.apiorder.enums.InboundOrderStatus;
import io.swiczka.github.apiorder.enums.OutboundOrderStatus;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import io.swiczka.github.apiorder.response.OutboundOrderResponse;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private static final Long TEST_LAYOUT_ID = 24L;
    private static final Instant TEST_TIME = Instant.parse("2026-08-20T10:00:00Z");

    private InboundOrderCreateDto createValidInboundDto() {
        return new InboundOrderCreateDto(
                TEST_COMPANY_ID,
                TEST_LAYOUT_ID,
                List.of("Package-A", "Package-B")
        );
    }

    private InboundOrderResponse createTestInboundResponse() {
        return new InboundOrderResponse(
                TEST_ORDER_ID,
                TEST_LAYOUT_ID,
                TEST_OPERATOR_ID,
                TEST_TIME,
                TEST_COMPANY_ID,
                InboundOrderStatus.PENDING
        );
    }

    private OutboundOrderCreateDto createValidOutboundDto() {
        return new OutboundOrderCreateDto(
                TEST_COMPANY_ID,
                TEST_LAYOUT_ID,
                List.of(1L, 2L)
        );
    }

    private OutboundOrderResponse createTestOutboundResponse() {
        return new OutboundOrderResponse(
                TEST_ORDER_ID,
                TEST_LAYOUT_ID,
                TEST_OPERATOR_ID,
                TEST_TIME,
                TEST_COMPANY_ID,
                OutboundOrderStatus.PENDING
        );
    }

    @Nested
    @DisplayName("POST /api/order/inbound")
    class AddNewInboundOrder {

        @Test
        @DisplayName("Should return 200 with inbound order response body")
        void addNewInboundOrder_ok() throws Exception {
            // given
            final InboundOrderCreateDto requestBody = createValidInboundDto();
            final InboundOrderResponse expectedResponse = createTestInboundResponse();

            when(orderService.addInboundOrder(any(InboundOrderCreateDto.class), eq(TEST_OPERATOR_ID))).thenReturn(expectedResponse);

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(TEST_ORDER_ID))
                    .andExpect(jsonPath("$.layoutId").value(TEST_LAYOUT_ID))
                    .andExpect(jsonPath("$.operatorId").value(TEST_OPERATOR_ID.toString()))
                    .andExpect(jsonPath("$.companyId").value(TEST_COMPANY_ID))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @DisplayName("Should return 400 when X-Guest-Id header is missing")
        void addNewInboundOrder_missingGuestIdHeader() throws Exception {
            // given
            final InboundOrderCreateDto requestBody = createValidInboundDto();

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
            final InboundOrderCreateDto requestBody = new InboundOrderCreateDto(
                    null,
                    TEST_LAYOUT_ID,
                    List.of("Package-A")
            );

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when packageNames list is empty")
        void addNewInboundOrder_emptyPackageList() throws Exception {
            // given
            final InboundOrderCreateDto requestBody = new InboundOrderCreateDto(
                    TEST_COMPANY_ID,
                    TEST_LAYOUT_ID,
                    List.of()
            );

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when layout id is null")
        void addNewInboundOrder_nullLayoutId() throws Exception {
            // given
            final InboundOrderCreateDto requestBody = new InboundOrderCreateDto(
                    TEST_COMPANY_ID,
                    null,
                    List.of()
            );

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when packageNames list exceeds 15 items")
        void addNewInboundOrder_tooManyPackages() throws Exception {
            // given
            final List<String> tooManyPackages = List.of(
                    "P1", "P2", "P3", "P4", "P5",
                    "P6", "P7", "P8", "P9", "P10",
                    "P11", "P12", "P13", "P14", "P15", "P16"
            );
            final InboundOrderCreateDto requestBody = new InboundOrderCreateDto(
                    TEST_COMPANY_ID,
                    TEST_LAYOUT_ID,
                    tooManyPackages
            );

            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when request body is missing")
        void addNewInboundOrder_missingBody() throws Exception {
            // when/then
            mockMvc.perform(post("/api/order/inbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("POST /api/order/outbound")
    class AddNewOutboundOrder {

        @Test
        @DisplayName("Should return 200 with outbound order response body")
        void addNewOutboundOrder_ok() throws Exception {
            // given
            final OutboundOrderCreateDto requestBody = createValidOutboundDto();
            final OutboundOrderResponse expectedResponse = createTestOutboundResponse();

            when(orderService.addOutboundOrder(any(OutboundOrderCreateDto.class), eq(TEST_OPERATOR_ID))).thenReturn(expectedResponse);

            // when/then
            mockMvc.perform(post("/api/order/outbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(TEST_ORDER_ID))
                    .andExpect(jsonPath("$.layoutId").value(TEST_LAYOUT_ID))
                    .andExpect(jsonPath("$.operatorId").value(TEST_OPERATOR_ID.toString()))
                    .andExpect(jsonPath("$.companyId").value(TEST_COMPANY_ID))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @DisplayName("Should return 400 when X-Guest-Id header is missing")
        void addNewOutboundOrder_missingGuestIdHeader() throws Exception {
            // given
            final OutboundOrderCreateDto requestBody = createValidOutboundDto();

            // when/then
            mockMvc.perform(post("/api/order/outbound")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when companyId is null")
        void addNewOutboundOrder_nullCompanyId() throws Exception {
            // given
            final OutboundOrderCreateDto requestBody = new OutboundOrderCreateDto(
                    null,
                    TEST_LAYOUT_ID,
                    List.of(1L)
            );

            // when/then
            mockMvc.perform(post("/api/order/outbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when layoutId is null")
        void addNewOutboundOrder_nullLayoutId() throws Exception {
            // given
            final OutboundOrderCreateDto requestBody = new OutboundOrderCreateDto(
                    TEST_COMPANY_ID,
                    null,
                    List.of(1L)
            );

            // when/then
            mockMvc.perform(post("/api/order/outbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when packageIds list is empty")
        void addNewOutboundOrder_emptyPackageList() throws Exception {
            // given
            final OutboundOrderCreateDto requestBody = new OutboundOrderCreateDto(
                    TEST_COMPANY_ID,
                    TEST_LAYOUT_ID,
                    List.of()
            );

            // when/then
            mockMvc.perform(post("/api/order/outbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/order/layout/{layoutId}/inbound")
    class GetInboundByLayout {

        @Test
        @DisplayName("Should return 200 with list of inbound orders for given layout and user")
        void getInboundByLayout_ok() throws Exception {
            // given
            final List<InboundOrderResponse> expectedOrders = List.of(createTestInboundResponse());

            when(orderService.getInboundByLayout(TEST_OPERATOR_ID, TEST_LAYOUT_ID)).thenReturn(expectedOrders);

            // when/then
            mockMvc.perform(get("/api/order/layout/" + TEST_LAYOUT_ID + "/inbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(TEST_ORDER_ID))
                    .andExpect(jsonPath("$[0].operatorId").value(TEST_OPERATOR_ID.toString()))
                    .andExpect(jsonPath("$[0].companyId").value(TEST_COMPANY_ID))
                    .andExpect(jsonPath("$[0].status").value("PENDING"));
        }

        @Test
        @DisplayName("Should return 200 with empty list when user has no inbound orders for layout")
        void getInboundByLayout_emptyList() throws Exception {
            // given
            when(orderService.getInboundByLayout(TEST_OPERATOR_ID, TEST_LAYOUT_ID)).thenReturn(List.of());

            // when/then
            mockMvc.perform(get("/api/order/layout/" + TEST_LAYOUT_ID + "/inbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 400 when X-Guest-Id header is missing")
        void getInboundByLayout_missingGuestIdHeader() throws Exception {
            // when/then
            mockMvc.perform(get("/api/order/layout/" + TEST_LAYOUT_ID + "/inbound"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/order/layout/{layoutId}/outbound")
    class GetOutboundByLayout {

        @Test
        @DisplayName("Should return 200 with list of outbound orders for given layout and user")
        void getOutboundByLayout_ok() throws Exception {
            // given
            final List<OutboundOrderResponse> expectedOrders = List.of(createTestOutboundResponse());

            when(orderService.getOutboundByLayout(TEST_OPERATOR_ID, TEST_LAYOUT_ID)).thenReturn(expectedOrders);

            // when/then
            mockMvc.perform(get("/api/order/layout/" + TEST_LAYOUT_ID + "/outbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(TEST_ORDER_ID))
                    .andExpect(jsonPath("$[0].layoutId").value(TEST_LAYOUT_ID))
                    .andExpect(jsonPath("$[0].operatorId").value(TEST_OPERATOR_ID.toString()))
                    .andExpect(jsonPath("$[0].companyId").value(TEST_COMPANY_ID))
                    .andExpect(jsonPath("$[0].status").value("PENDING"));
        }

        @Test
        @DisplayName("Should return 200 with empty list when user has no outbound orders for layout")
        void getOutboundByLayout_emptyList() throws Exception {
            // given
            when(orderService.getOutboundByLayout(TEST_OPERATOR_ID, TEST_LAYOUT_ID)).thenReturn(List.of());

            // when/then
            mockMvc.perform(get("/api/order/layout/" + TEST_LAYOUT_ID + "/outbound")
                            .header("X-Guest-Id", TEST_OPERATOR_ID.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should return 400 when X-Guest-Id header is missing")
        void getOutboundByLayout_missingGuestIdHeader() throws Exception {
            // when/then
            mockMvc.perform(get("/api/order/layout/" + TEST_LAYOUT_ID + "/outbound"))
                    .andExpect(status().isBadRequest());
        }
    }
}
