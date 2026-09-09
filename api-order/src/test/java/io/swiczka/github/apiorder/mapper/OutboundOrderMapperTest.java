package io.swiczka.github.apiorder.mapper;

import io.swiczka.github.apiorder.entity.OutboundOrder;
import io.swiczka.github.apiorder.enums.OutboundOrderStatus;
import io.swiczka.github.apiorder.response.OutboundOrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboundOrderMapperTest {

    private OutboundOrder testOrder;

    private static final UUID TEST_OPERATOR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long TEST_COMPANY_ID = 42L;
    private static final Long TEST_LAYOUT_ID = 24L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Instant TEST_TIME = Instant.parse("2026-08-20T10:00:00Z");

    @BeforeEach
    void setUp() {
        testOrder = new OutboundOrder(
                TEST_LAYOUT_ID,
                TEST_OPERATOR_ID,
                TEST_COMPANY_ID,
                TEST_TIME,
                OutboundOrderStatus.PENDING
        );
        testOrder.setId(TEST_ORDER_ID);
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("Should return mapped OutboundOrderResponse from OutboundOrder entity")
        void toDto_ok() {
            // given - in setUp()

            // when
            final OutboundOrderResponse result = OutboundOrderMapper.toDto(testOrder);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(testOrder.getId());
            assertThat(result.layoutId()).isEqualTo(testOrder.getLayoutId());
            assertThat(result.operatorId()).isEqualTo(testOrder.getOperatorId());
            assertThat(result.createdAt()).isEqualTo(testOrder.getCreatedAt());
            assertThat(result.companyId()).isEqualTo(testOrder.getCompanyId());
            assertThat(result.status()).isEqualTo(testOrder.getStatus());
        }

        @Test
        @DisplayName("Should return null when given null entity")
        void toDto_nullEntity() {
            // when
            final OutboundOrderResponse result = OutboundOrderMapper.toDto(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should correctly map each OutboundOrderStatus value")
        void toDto_mapsStatusCorrectly() {
            // given
            for (final OutboundOrderStatus status : OutboundOrderStatus.values()) {
                final OutboundOrder orderWithStatus = new OutboundOrder(
                        TEST_LAYOUT_ID,
                        TEST_OPERATOR_ID,
                        TEST_COMPANY_ID,
                        TEST_TIME,
                        status
                );

                // when
                final OutboundOrderResponse result = OutboundOrderMapper.toDto(orderWithStatus);

                // then
                assertThat(result).isNotNull();
                assertThat(result.status()).isEqualTo(status);
                assertThat(result.layoutId()).isEqualTo(TEST_LAYOUT_ID);
            }
        }
    }
}
