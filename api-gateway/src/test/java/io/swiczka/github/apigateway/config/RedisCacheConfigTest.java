package io.swiczka.github.apigateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RedisCacheConfigTest {

    @Test
    @DisplayName("should configure redis cache with 10 minutes TTL and disable null caching")
    void shouldConfigureRedisCacheProperly() {
        // given
        final RedisCacheConfig redisCacheConfig = new RedisCacheConfig();

        // when
        final RedisCacheConfiguration config = redisCacheConfig.cacheConfiguration();

        // then
        assertThat(config).isNotNull();
        assertThat(config.getTtlFunction().getTimeToLive("layout_owners", null))
                .isEqualTo(Duration.ofMinutes(10));
        assertThat(config.getAllowCacheNullValues()).isFalse();
        assertThat(config.getValueSerializationPair()).isNotNull();
    }

    @Test
    @DisplayName("should serialize and deserialize LayoutOwnerDto")
    void shouldSerializeAndDeserializeLayoutOwnerDto() {
        // given
        final RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        final RedisCacheConfiguration config = redisCacheConfig.cacheConfiguration();
        final io.swiczka.github.sharedcommon.dto.LayoutOwnerDto dto =
                new io.swiczka.github.sharedcommon.dto.LayoutOwnerDto(22L, java.util.UUID.randomUUID());

        // when
        final byte[] bytes = config.getValueSerializationPair().write(dto).array();
        final Object deserialized = config.getValueSerializationPair().read(java.nio.ByteBuffer.wrap(bytes));

        // then
        assertThat(deserialized).isInstanceOf(io.swiczka.github.sharedcommon.dto.LayoutOwnerDto.class);
        assertThat(deserialized).isEqualTo(dto);
    }

    @Test
    @DisplayName("should serialize and deserialize Optional of LayoutOwnerDto")
    void shouldSerializeAndDeserializeOptional() {
        // given
        final RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        final RedisCacheConfiguration config = redisCacheConfig.cacheConfiguration();
        final io.swiczka.github.sharedcommon.dto.LayoutOwnerDto dto =
                new io.swiczka.github.sharedcommon.dto.LayoutOwnerDto(22L, java.util.UUID.randomUUID());
        final java.util.Optional<io.swiczka.github.sharedcommon.dto.LayoutOwnerDto> optional =
                java.util.Optional.of(dto);

        // when
        final byte[] bytes = config.getValueSerializationPair().write(optional).array();
        final Object deserialized = config.getValueSerializationPair().read(java.nio.ByteBuffer.wrap(bytes));

        // then
        System.out.println("Optional serialized as JSON: " + new String(bytes));
        System.out.println("Deserialized: " + deserialized);
    }
}
