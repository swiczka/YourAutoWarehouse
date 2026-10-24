package io.swiczka.github.apigateway.client;

import io.swiczka.github.apigateway.exceptions.WarehouseApiException;
import io.swiczka.github.sharedcommon.dto.LayoutOwnerDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.ConnectException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WarehouseApiClientTest {

    @Test
    @DisplayName("should return layout owner when warehouse API responds with success")
    void shouldReturnLayoutOwnerWhenApiRespondsWithSuccess() {
        // given
        final RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8082");
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final WarehouseApiClient client = new WarehouseApiClient(builder.build());

        final Long layoutId = 35L;
        final String guestId = UUID.randomUUID().toString();
        final String jsonResponse = """
                {
                    "id": 35,
                    "userId": "%s"
                }
                """.formatted(guestId);

        server.expect(requestTo("http://localhost:8082/api/warehouse/layout/35/owner"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Guest-Id", guestId))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // when
        final Optional<LayoutOwnerDto> result = client.getLayoutOwner(layoutId, guestId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(layoutId);
        assertThat(result.get().userId().toString()).isEqualTo(guestId);
        server.verify();
    }

    @Test
    @DisplayName("should return empty when warehouse API returns 404")
    void shouldReturnEmptyWhenApiReturnsNotFound() {
        // given
        final RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8082");
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final WarehouseApiClient client = new WarehouseApiClient(builder.build());

        final Long layoutId = 999L;
        final String guestId = UUID.randomUUID().toString();

        server.expect(requestTo("http://localhost:8082/api/warehouse/layout/999/owner"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        // when
        final Optional<LayoutOwnerDto> result = client.getLayoutOwner(layoutId, guestId);

        // then
        assertThat(result).isEmpty();
        server.verify();
    }

    @Test
    @DisplayName("should throw WarehouseApiException when warehouse API is unreachable")
    void shouldThrowWhenApiIsUnreachable() {
        // given
        final RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8082");
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final WarehouseApiClient client = new WarehouseApiClient(builder.build());

        final Long layoutId = 24L;
        final String guestId = UUID.randomUUID().toString();

        server.expect(requestTo("http://localhost:8082/api/warehouse/layout/24/owner"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withException(new ConnectException("Connection refused")));

        // when / then
        assertThatThrownBy(() -> client.getLayoutOwner(layoutId, guestId))
                .isInstanceOf(WarehouseApiException.class);
        server.verify();
    }

    @Test
    @DisplayName("should throw WarehouseApiException when warehouse API returns 5xx")
    void shouldThrowWhenApiReturnsServerError() {
        // given
        final RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8082");
        final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        final WarehouseApiClient client = new WarehouseApiClient(builder.build());

        final Long layoutId = 24L;
        final String guestId = UUID.randomUUID().toString();

        server.expect(requestTo("http://localhost:8082/api/warehouse/layout/24/owner"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // when / then
        assertThatThrownBy(() -> client.getLayoutOwner(layoutId, guestId))
                .isInstanceOf(WarehouseApiException.class);
        server.verify();
    }
}
