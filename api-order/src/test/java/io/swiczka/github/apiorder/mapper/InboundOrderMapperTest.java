package io.swiczka.github.apiorder.mapper;

import io.swiczka.github.apiorder.dto.InboundOrderReadDto;
import io.swiczka.github.apiorder.entity.InboundOrder;
import io.swiczka.github.apiorder.enums.InboundOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InboundOrderMapperTest {

    private InboundOrder testOrder;

    private static final UUID TEST_OPERATOR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long TEST_COMPANY_ID = 42L;
    private static final Long TEST_ORDER_ID = 1L;
    private static final Instant TEST_TIME = Instant.parse("2026-08-20T10:00:00Z");

    @BeforeEach
    void setUp() {
        testOrder = new InboundOrder(
                TEST_OPERATOR_ID,
                TEST_COMPANY_ID,
                TEST_TIME,
                InboundOrderStatus.PENDING
        );
        testOrder.setId(TEST_ORDER_ID);
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("Should return mapped InboundOrderReadDto from InboundOrder entity")
        void toDto_ok() {
            // given - in setUp()

            // when
            final InboundOrderReadDto result = InboundOrderMapper.toDto(testOrder);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(testOrder.getId());
            assertThat(result.operatorId()).isEqualTo(testOrder.getOperatorId());
            assertThat(result.createdAt()).isEqualTo(testOrder.getCreatedAt());
            assertThat(result.companyId()).isEqualTo(testOrder.getCompanyId());
            assertThat(result.status()).isEqualTo(testOrder.getStatus());
        }

        @Test
        @DisplayName("Should correctly map each InboundOrderStatus value")
        void toDto_mapsStatusCorrectly() {
            // given
            for (final InboundOrderStatus status : InboundOrderStatus.values()) {
                final InboundOrder orderWithStatus = new InboundOrder(
                        TEST_OPERATOR_ID,
                        TEST_COMPANY_ID,
                        TEST_TIME,
                        status
                );

                // when
                final InboundOrderReadDto result = InboundOrderMapper.toDto(orderWithStatus);

                // then
                assertThat(result.status()).isEqualTo(status);
            }
        }
    }
}
