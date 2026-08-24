package io.swiczka.github.apiorder;

import io.swiczka.github.apiorder.dao.InboundOrderDAO;
import io.swiczka.github.apiorder.dao.OutboundOrderDAO;
import io.swiczka.github.apiorder.dto.InboundOrderCreateDto;
import io.swiczka.github.apiorder.dto.InboundOrderReadDto;
import io.swiczka.github.apiorder.entity.InboundOrder;
import io.swiczka.github.apiorder.enums.InboundOrderStatus;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private InboundOrderDAO inboundDAO;

    @Mock
    private OutboundOrderDAO outboundDAO;

    @InjectMocks
    private OrderService orderService;

    private static final UUID TEST_OPERATOR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long TEST_COMPANY_ID = 42L;
    private static final Long TEST_ORDER_ID = 1L;

    private InboundOrderCreateDto testCreateDto;
    private InboundOrder testInboundOrder;

    @BeforeEach
    void setUp() {
        testCreateDto = new InboundOrderCreateDto(
                TEST_OPERATOR_ID,
                TEST_COMPANY_ID,
                List.of("Package-A", "Package-B")
        );

        testInboundOrder = new InboundOrder(
                TEST_OPERATOR_ID,
                TEST_COMPANY_ID,
                Instant.parse("2026-08-20T10:00:00Z"),
                InboundOrderStatus.PENDING
        );
        testInboundOrder.setId(TEST_ORDER_ID);
    }

    @Nested
    @DisplayName("addInboundOrder")
    class AddInboundOrder {

        @Test
        @DisplayName("Should save new inbound order and return response with correct package count")
        void addInboundOrder_ok() {
            // given
            ArgumentCaptor<InboundOrder> orderCaptor = ArgumentCaptor.forClass(InboundOrder.class);

            // when
            InboundOrderResponse response = orderService.addInboundOrder(testCreateDto);

            // then - what went to DAO
            verify(inboundDAO).save(orderCaptor.capture());
            InboundOrder savedEntity = orderCaptor.getValue();

            assertThat(savedEntity.getOperatorId()).isEqualTo(TEST_OPERATOR_ID);
            assertThat(savedEntity.getCompanyId()).isEqualTo(TEST_COMPANY_ID);
            assertThat(savedEntity.getStatus()).isEqualTo(InboundOrderStatus.PENDING);
            assertThat(savedEntity.getCreatedAt()).isNotNull();

            // then - what is returned
            assertThat(response).isNotNull();
            assertThat(response.operatorId()).isEqualTo(TEST_OPERATOR_ID);
            assertThat(response.companyId()).isEqualTo(TEST_COMPANY_ID);
            assertThat(response.packageCount()).isEqualTo(testCreateDto.packageNames().size());
        }

        @Test
        @DisplayName("Should set status to PENDING on newly created order")
        void addInboundOrder_statusIsPending() {
            // given
            ArgumentCaptor<InboundOrder> orderCaptor = ArgumentCaptor.forClass(InboundOrder.class);

            // when
            orderService.addInboundOrder(testCreateDto);

            // then
            verify(inboundDAO).save(orderCaptor.capture());
            assertThat(orderCaptor.getValue().getStatus()).isEqualTo(InboundOrderStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("getInboundByUser")
    class GetInboundByUser {

        @Test
        @DisplayName("Should return mapped list of InboundOrderReadDto for given userId")
        void getInboundByUser_ok() {
            // given
            when(inboundDAO.getInboundByUser(TEST_OPERATOR_ID)).thenReturn(List.of(testInboundOrder));

            // when
            List<InboundOrderReadDto> result = orderService.getInboundByUser(TEST_OPERATOR_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);

            InboundOrderReadDto dto = result.getFirst();
            assertThat(dto.id()).isEqualTo(TEST_ORDER_ID);
            assertThat(dto.operatorId()).isEqualTo(TEST_OPERATOR_ID);
            assertThat(dto.companyId()).isEqualTo(TEST_COMPANY_ID);
            assertThat(dto.status()).isEqualTo(InboundOrderStatus.PENDING);
        }

        @Test
        @DisplayName("Should return empty list when user has no inbound orders")
        void getInboundByUser_emptyList() {
            // given
            when(inboundDAO.getInboundByUser(TEST_OPERATOR_ID)).thenReturn(List.of());

            // when
            List<InboundOrderReadDto> result = orderService.getInboundByUser(TEST_OPERATOR_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }
}
