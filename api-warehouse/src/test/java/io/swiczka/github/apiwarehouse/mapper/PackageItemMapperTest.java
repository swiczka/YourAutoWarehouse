package io.swiczka.github.apiwarehouse.mapper;

import io.swiczka.github.apiwarehouse.entity.PackageItem;
import io.swiczka.github.apiwarehouse.enums.PackageStatus;
import io.swiczka.github.apiwarehouse.packageitem.response.PackageItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PackageItemMapperTest {

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("Should return mapped PackageItemResponse from PackageItem entity")
        void toDto_ok() {
            // given
            final PackageItem entity = new PackageItem(
                    1L,
                    "Package1",
                    10L,
                    20L,
                    3,
                    4,
                    PackageStatus.STORED
            );
            entity.setId(100L);

            // when
            final PackageItemResponse response = PackageItemMapper.toDto(entity);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(100L);
            assertThat(response.name()).isEqualTo("Package1");
            assertThat(response.inboundOrderId()).isEqualTo(10L);
            assertThat(response.outboundOrderId()).isEqualTo(20L);
            assertThat(response.x()).isEqualTo(3);
            assertThat(response.y()).isEqualTo(4);
            assertThat(response.status()).isEqualTo(PackageStatus.STORED);
            assertThat(response.layoutId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return null when given null entity")
        void toDto_nullParameter() {
            // given
            final PackageItem entity = null;

            // when
            final PackageItemResponse response = PackageItemMapper.toDto(entity);

            // then
            assertThat(response).isNull();
        }
    }
}
