package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.apiwarehouse.entity.PackageItem;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaPackageItemDAOTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<PackageItem> typedQuery;

    @InjectMocks
    private JpaPackageItemDAO jpaPackageItemDAO;

    private PackageItem testPackageItem;

    @BeforeEach
    void setUp() {
        testPackageItem = new PackageItem("TestPackage", 100L, null, 2, 5);
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Should persist when packageItem has no id")
        void save_persistWhenNoId() {
            // given - testPackageItem.id is null

            // when
            jpaPackageItemDAO.save(testPackageItem);

            // then
            verify(entityManager).persist(testPackageItem);
        }

        @Test
        @DisplayName("Should merge when packageItem already has an id")
        void save_mergeWhenHasId() {
            // given
            testPackageItem.setId(1L);

            // when
            jpaPackageItemDAO.save(testPackageItem);

            // then
            verify(entityManager).merge(testPackageItem);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Should return Optional with entity when found")
        void findById_found() {
            // given
            testPackageItem.setId(1L);
            when(entityManager.find(PackageItem.class, 1L)).thenReturn(testPackageItem);

            // when
            final Optional<PackageItem> result = jpaPackageItemDAO.findById(1L);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("TestPackage");
        }

        @Test
        @DisplayName("Should return empty Optional when not found")
        void findById_notFound() {
            // given
            when(entityManager.find(PackageItem.class, 999L)).thenReturn(null);

            // when
            final Optional<PackageItem> result = jpaPackageItemDAO.findById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByInboundOrderId")
    class FindByInboundOrderId {

        @Test
        @DisplayName("Should return list of packages for given inboundOrderId")
        void findByInboundOrderId_ok() {
            // given
            when(entityManager.createQuery(anyString(), eq(PackageItem.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("inboundOrderId", 100L)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of(testPackageItem));

            // when
            final List<PackageItem> result = jpaPackageItemDAO.findByInboundOrderId(100L);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getInboundOrderId()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("findByOutboundOrderId")
    class FindByOutboundOrderId {

        @Test
        @DisplayName("Should return list of packages for given outboundOrderId")
        void findByOutboundOrderId_ok() {
            // given
            when(entityManager.createQuery(anyString(), eq(PackageItem.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("outboundOrderId", 200L)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of(testPackageItem));

            // when
            final List<PackageItem> result = jpaPackageItemDAO.findByOutboundOrderId(200L);

            // then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findByCoordinates")
    class FindByCoordinates {

        @Test
        @DisplayName("Should return Optional with entity when package found at coordinates")
        void findByCoordinates_found() {
            // given
            when(entityManager.createQuery(anyString(), eq(PackageItem.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("x", 2)).thenReturn(typedQuery);
            when(typedQuery.setParameter("y", 5)).thenReturn(typedQuery);
            when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of(testPackageItem));

            // when
            final Optional<PackageItem> result = jpaPackageItemDAO.findByCoordinates(2, 5);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getX()).isEqualTo(2);
            assertThat(result.get().getY()).isEqualTo(5);
        }

        @Test
        @DisplayName("Should return empty Optional when no package at coordinates")
        void findByCoordinates_notFound() {
            // given
            when(entityManager.createQuery(anyString(), eq(PackageItem.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("x", 9)).thenReturn(typedQuery);
            when(typedQuery.setParameter("y", 9)).thenReturn(typedQuery);
            when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of());

            // when
            final Optional<PackageItem> result = jpaPackageItemDAO.findByCoordinates(9, 9);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return Optional with entity when package found by layoutId and coordinates")
        void findByCoordinates_withLayoutId_found() {
            // given
            when(entityManager.createQuery(anyString(), eq(PackageItem.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("layoutId", 1L)).thenReturn(typedQuery);
            when(typedQuery.setParameter("x", 2)).thenReturn(typedQuery);
            when(typedQuery.setParameter("y", 5)).thenReturn(typedQuery);
            when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of(testPackageItem));

            // when
            final Optional<PackageItem> result = jpaPackageItemDAO.findByCoordinates(1L, 2, 5);

            // then
            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("findByLayoutId")
    class FindByLayoutId {

        @Test
        @DisplayName("Should return list of packages for given layoutId")
        void findByLayoutId_ok() {
            // given
            when(entityManager.createQuery(anyString(), eq(PackageItem.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("layoutId", 1L)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of(testPackageItem));

            // when
            final List<PackageItem> result = jpaPackageItemDAO.findByLayoutId(1L);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should return packages filtered by layoutId and inboundOrderId")
        void findByLayoutIdAndInboundOrderId_ok() {
            // given
            when(entityManager.createQuery(anyString(), eq(PackageItem.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("layoutId", 1L)).thenReturn(typedQuery);
            when(typedQuery.setParameter("inboundOrderId", 100L)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of(testPackageItem));

            // when
            final List<PackageItem> result = jpaPackageItemDAO.findByLayoutIdAndInboundOrderId(1L, 100L);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should return packages filtered by layoutId and outboundOrderId")
        void findByLayoutIdAndOutboundOrderId_ok() {
            // given
            when(entityManager.createQuery(anyString(), eq(PackageItem.class))).thenReturn(typedQuery);
            when(typedQuery.setParameter("layoutId", 1L)).thenReturn(typedQuery);
            when(typedQuery.setParameter("outboundOrderId", 200L)).thenReturn(typedQuery);
            when(typedQuery.getResultList()).thenReturn(List.of(testPackageItem));

            // when
            final List<PackageItem> result = jpaPackageItemDAO.findByLayoutIdAndOutboundOrderId(1L, 200L);

            // then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findOccupiedCoordinates")
    class FindOccupiedCoordinates {

        @Test
        @DisplayName("Should return set of coordinates for packages with active statuses in specific layout")
        void findOccupiedCoordinates_withLayoutId_ok() {
            // given
            final Coordinate coordinate = new Coordinate(2, 5);
            @SuppressWarnings("unchecked")
            final TypedQuery<Coordinate> coordQuery = org.mockito.Mockito.mock(TypedQuery.class);

            when(entityManager.createQuery(anyString(), eq(Coordinate.class))).thenReturn(coordQuery);
            when(coordQuery.setParameter(eq("layoutId"), eq(1L))).thenReturn(coordQuery);
            when(coordQuery.setParameter(eq("statuses"), any())).thenReturn(coordQuery);
            when(coordQuery.getResultList()).thenReturn(List.of(coordinate));

            // when
            final Set<Coordinate> result = jpaPackageItemDAO.findOccupiedCoordinates(1L);

            // then
            assertThat(result).containsExactly(coordinate);
        }

        @Test
        @DisplayName("Should return set of coordinates for packages with active statuses")
        void findOccupiedCoordinates_ok() {
            // given
            final Coordinate coordinate = new Coordinate(2, 5);
            @SuppressWarnings("unchecked")
            final TypedQuery<Coordinate> coordQuery = org.mockito.Mockito.mock(TypedQuery.class);

            when(entityManager.createQuery(anyString(), eq(Coordinate.class))).thenReturn(coordQuery);
            when(coordQuery.setParameter(eq("statuses"), any())).thenReturn(coordQuery);
            when(coordQuery.getResultList()).thenReturn(List.of(coordinate));

            // when
            final Set<Coordinate> result = jpaPackageItemDAO.findOccupiedCoordinates();

            // then
            assertThat(result).containsExactly(coordinate);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Should remove directly when entity is managed")
        void delete_managedEntity() {
            // given
            when(entityManager.contains(testPackageItem)).thenReturn(true);

            // when
            jpaPackageItemDAO.delete(testPackageItem);

            // then
            verify(entityManager).remove(testPackageItem);
        }

        @Test
        @DisplayName("Should find and remove when entity is detached")
        void delete_detachedEntity() {
            // given
            testPackageItem.setId(1L);
            when(entityManager.contains(testPackageItem)).thenReturn(false);
            when(entityManager.find(PackageItem.class, 1L)).thenReturn(testPackageItem);

            // when
            jpaPackageItemDAO.delete(testPackageItem);

            // then
            verify(entityManager).remove(testPackageItem);
        }
    }
}
