package io.swiczka.github.apiorder;

import io.swiczka.github.apiorder.dao.InboundOrderDAO;
import io.swiczka.github.apiorder.dao.OutboundOrderDAO;
import io.swiczka.github.apiorder.dto.InboundOrderCreateDto;
import io.swiczka.github.apiorder.dto.OutboundOrderCreateDto;
import io.swiczka.github.apiorder.entity.InboundOrder;
import io.swiczka.github.apiorder.entity.OutboundOrder;
import io.swiczka.github.apiorder.enums.InboundOrderStatus;
import io.swiczka.github.apiorder.enums.OutboundOrderStatus;
import io.swiczka.github.apiorder.producer.OrderEventProducer;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import io.swiczka.github.apiorder.response.OutboundOrderResponse;
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

    @Mock
    private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderService orderService;

    private static final UUID TEST_OPERATOR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long TEST_COMPANY_ID = 42L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Instant TEST_TIME = Instant.parse("2026-08-20T10:00:00Z");
    private static final Long TEST_LAYOUT_ID = 24L;


    private InboundOrderCreateDto testInboundCreateDto;
    private InboundOrder testInboundOrder;

    private OutboundOrderCreateDto testOutboundCreateDto;
    private OutboundOrder testOutboundOrder;

    @BeforeEach
    void setUp() {
        testInboundCreateDto = new InboundOrderCreateDto(
                TEST_COMPANY_ID,
                TEST_LAYOUT_ID,
                List.of("Package-A", "Package-B")
        );

        testInboundOrder = new InboundOrder(
                TEST_LAYOUT_ID,
                TEST_OPERATOR_ID,
                TEST_COMPANY_ID,
                TEST_TIME,
                InboundOrderStatus.PENDING
        );
        testInboundOrder.setId(TEST_ORDER_ID);

        testOutboundCreateDto = new OutboundOrderCreateDto(
                TEST_COMPANY_ID,
                TEST_LAYOUT_ID,
                List.of(1, 2)
        );

        testOutboundOrder = new OutboundOrder(
                TEST_LAYOUT_ID,
                TEST_OPERATOR_ID,
                TEST_COMPANY_ID,
                TEST_TIME,
                OutboundOrderStatus.PENDING
        );
        testOutboundOrder.setId(TEST_ORDER_ID);
    }

    @Nested
    @DisplayName("addInboundOrder")
    class AddInboundOrder {

        @Test
        @DisplayName("Should save new inbound order and return response")
        void addInboundOrder_ok() {
            // given
            final ArgumentCaptor<InboundOrder> orderCaptor = ArgumentCaptor.forClass(InboundOrder.class);

            // when
            final InboundOrderResponse response = orderService.addInboundOrder(testInboundCreateDto, TEST_OPERATOR_ID);

            // then - what went to DAO
            verify(inboundDAO).save(orderCaptor.capture());
            final InboundOrder savedEntity = orderCaptor.getValue();

            assertThat(savedEntity.getLayoutId()).isEqualTo(TEST_LAYOUT_ID);
            assertThat(savedEntity.getOperatorId()).isEqualTo(TEST_OPERATOR_ID);
            assertThat(savedEntity.getCompanyId()).isEqualTo(TEST_COMPANY_ID);
            assertThat(savedEntity.getStatus()).isEqualTo(InboundOrderStatus.PENDING);
            assertThat(savedEntity.getCreatedAt()).isNotNull();

            // then - what is returned
            assertThat(response).isNotNull();
            assertThat(response.layoutId()).isEqualTo(TEST_LAYOUT_ID);
            assertThat(response.operatorId()).isEqualTo(TEST_OPERATOR_ID);
            assertThat(response.companyId()).isEqualTo(TEST_COMPANY_ID);
            assertThat(response.status()).isEqualTo(InboundOrderStatus.PENDING);
            assertThat(response.createdAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("getInboundByUser")
    class GetInboundByUser {

        @Test
        @DisplayName("Should return mapped list of InboundOrderResponse for given userId")
        void getInboundByUser_ok() {
            // given
            when(inboundDAO.getInboundByUser(TEST_OPERATOR_ID)).thenReturn(List.of(testInboundOrder));

            // when
            final List<InboundOrderResponse> result = orderService.getInboundByUser(TEST_OPERATOR_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);

            final InboundOrderResponse dto = result.getFirst();
            assertThat(dto.id()).isEqualTo(TEST_ORDER_ID);
            assertThat(dto.layoutId()).isEqualTo(TEST_LAYOUT_ID);
            assertThat(dto.operatorId()).isEqualTo(TEST_OPERATOR_ID);
            assertThat(dto.companyId()).isEqualTo(TEST_COMPANY_ID);
            assertThat(dto.status()).isEqualTo(InboundOrderStatus.PENDING);
            assertThat(dto.createdAt()).isEqualTo(TEST_TIME);
        }

        @Test
        @DisplayName("Should return empty list when user has no inbound orders")
        void getInboundByUser_emptyList() {
            // given
            when(inboundDAO.getInboundByUser(TEST_OPERATOR_ID)).thenReturn(List.of());

            // when
            final List<InboundOrderResponse> result = orderService.getInboundByUser(TEST_OPERATOR_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("addOutboundOrder")
    class AddOutboundOrder {

        @Test
        @DisplayName("Should save new outbound order and return response")
        void addOutboundOrder_ok() {
            // given
            final ArgumentCaptor<OutboundOrder> orderCaptor = ArgumentCaptor.forClass(OutboundOrder.class);

            // when
            final OutboundOrderResponse response = orderService.addOutboundOrder(testOutboundCreateDto, TEST_OPERATOR_ID);

            // then - what went to DAO
            verify(outboundDAO).save(orderCaptor.capture());
            final OutboundOrder savedEntity = orderCaptor.getValue();

            assertThat(savedEntity.getLayoutId()).isEqualTo(TEST_LAYOUT_ID);
            assertThat(savedEntity.getOperatorId()).isEqualTo(TEST_OPERATOR_ID);
            assertThat(savedEntity.getCompanyId()).isEqualTo(TEST_COMPANY_ID);
            assertThat(savedEntity.getStatus()).isEqualTo(OutboundOrderStatus.PENDING);
            assertThat(savedEntity.getCreatedAt()).isNotNull();

            // then - what is returned
            assertThat(response).isNotNull();
            assertThat(response.layoutId()).isEqualTo(TEST_LAYOUT_ID);
            assertThat(response.operatorId()).isEqualTo(TEST_OPERATOR_ID);
            assertThat(response.companyId()).isEqualTo(TEST_COMPANY_ID);
            assertThat(response.status()).isEqualTo(OutboundOrderStatus.PENDING);
            assertThat(response.createdAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("getOutboundByUser")
    class GetOutboundByUser {

        @Test
        @DisplayName("Should return mapped list of OutboundOrderResponse for given userId")
        void getOutboundByUser_ok() {
            // given
            when(outboundDAO.getOutboundByUser(TEST_OPERATOR_ID)).thenReturn(List.of(testOutboundOrder));

            // when
            final List<OutboundOrderResponse> result = orderService.getOutboundByUser(TEST_OPERATOR_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);

            final OutboundOrderResponse dto = result.getFirst();
            assertThat(dto.id()).isEqualTo(TEST_ORDER_ID);
            assertThat(dto.layoutId()).isEqualTo(TEST_LAYOUT_ID);
            assertThat(dto.operatorId()).isEqualTo(TEST_OPERATOR_ID);
            assertThat(dto.companyId()).isEqualTo(TEST_COMPANY_ID);
            assertThat(dto.status()).isEqualTo(OutboundOrderStatus.PENDING);
            assertThat(dto.createdAt()).isEqualTo(TEST_TIME);
        }

        @Test
        @DisplayName("Should return empty list when user has no outbound orders")
        void getOutboundByUser_emptyList() {
            // given
            when(outboundDAO.getOutboundByUser(TEST_OPERATOR_ID)).thenReturn(List.of());

            // when
            final List<OutboundOrderResponse> result = orderService.getOutboundByUser(TEST_OPERATOR_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }
    }
}
