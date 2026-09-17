package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.sharedcommon.dto.LayoutOwnerDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaLayoutDAOTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<LayoutOwnerDto> typedQuery;

    @InjectMocks
    private JpaLayoutDAO jpaLayoutDAO;

    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Nested
    @DisplayName("findOwnerById")
    class FindOwnerById {

        @Test
        @DisplayName("Should return LayoutOwnerDto when layout exists")
        void findOwnerById_found() {
            // given
            final LayoutOwnerDto ownerDto = new LayoutOwnerDto(1L, TEST_USER_ID);
            when(entityManager.createQuery(anyString(), eq(LayoutOwnerDto.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", 1L)).thenReturn(typedQuery);
            when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of(ownerDto));

            // when
            final Optional<LayoutOwnerDto> result = jpaLayoutDAO.findOwnerById(1L);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(1L);
            assertThat(result.get().userId()).isEqualTo(TEST_USER_ID);
        }

        @Test
        @DisplayName("Should return empty Optional when layout does not exist")
        void findOwnerById_notFound() {
            // given
            when(entityManager.createQuery(anyString(), eq(LayoutOwnerDto.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("id", 999L)).thenReturn(typedQuery);
            when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of());

            // when
            final Optional<LayoutOwnerDto> result = jpaLayoutDAO.findOwnerById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }
}
