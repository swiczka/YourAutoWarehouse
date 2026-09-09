package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.apiwarehouse.entity.PackageItem;
import io.swiczka.github.apiwarehouse.enums.PackageStatus;
import io.swiczka.github.sharedcommon.helpers.Coordinate;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class JpaPackageItemDAO implements PackageItemDAO {

    private final EntityManager entityManager;

    @Autowired
    public JpaPackageItemDAO(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(final PackageItem packageItem) {
        if (packageItem.getId() == null) {
            this.entityManager.persist(packageItem);
            return;
        }
        this.entityManager.merge(packageItem);
    }

    @Override
    public Optional<PackageItem> findById(final Long id) {
        final PackageItem packageItem = this.entityManager.find(PackageItem.class, id);
        return Optional.ofNullable(packageItem);
    }

    @Override
    public List<PackageItem> findByLayoutId(final Long layoutId) {
        return this.entityManager.createQuery(
                        "FROM PackageItem p WHERE p.layoutId = :layoutId", PackageItem.class)
                .setParameter("layoutId", layoutId)
                .getResultList();
    }

    @Override
    public List<PackageItem> findByInboundOrderId(final Long inboundOrderId) {
        return this.entityManager.createQuery(
                        "FROM PackageItem p WHERE p.inboundOrderId = :inboundOrderId", PackageItem.class)
                .setParameter("inboundOrderId", inboundOrderId)
                .getResultList();
    }

    @Override
    public List<PackageItem> findByOutboundOrderId(final Long outboundOrderId) {
        return this.entityManager.createQuery(
                        "FROM PackageItem p WHERE p.outboundOrderId = :outboundOrderId", PackageItem.class)
                .setParameter("outboundOrderId", outboundOrderId)
                .getResultList();
    }

    @Override
    public List<PackageItem> findByLayoutIdAndInboundOrderId(final Long layoutId, final Long inboundOrderId) {
        return this.entityManager.createQuery(
                        "FROM PackageItem p WHERE p.layoutId = :layoutId AND p.inboundOrderId = :inboundOrderId", PackageItem.class)
                .setParameter("layoutId", layoutId)
                .setParameter("inboundOrderId", inboundOrderId)
                .getResultList();
    }

    @Override
    public List<PackageItem> findByLayoutIdAndOutboundOrderId(final Long layoutId, final Long outboundOrderId) {
        return this.entityManager.createQuery(
                        "FROM PackageItem p WHERE p.layoutId = :layoutId AND p.outboundOrderId = :outboundOrderId", PackageItem.class)
                .setParameter("layoutId", layoutId)
                .setParameter("outboundOrderId", outboundOrderId)
                .getResultList();
    }

    @Override
    public Optional<PackageItem> findByCoordinates(final Long layoutId, final int x, final int y) {
        final List<PackageItem> items = this.entityManager.createQuery(
                        "FROM PackageItem p WHERE p.layoutId = :layoutId AND p.x = :x AND p.y = :y", PackageItem.class)
                .setParameter("layoutId", layoutId)
                .setParameter("x", x)
                .setParameter("y", y)
                .setMaxResults(1)
                .getResultList();
        if (items.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(items.getFirst());
    }

    @Override
    public Optional<PackageItem> findByCoordinates(final int x, final int y) {
        final List<PackageItem> items = this.entityManager.createQuery(
                        "FROM PackageItem p WHERE p.x = :x AND p.y = :y", PackageItem.class)
                .setParameter("x", x)
                .setParameter("y", y)
                .setMaxResults(1)
                .getResultList();
        if (items.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(items.getFirst());
    }

    @Override
    public Set<Coordinate> findOccupiedCoordinates(final Long layoutId) {
        final List<Coordinate> results = this.entityManager.createQuery(
                        "SELECT new io.swiczka.github.sharedcommon.helpers.Coordinate(p.x, p.y) " +
                                "FROM PackageItem p " +
                                "WHERE p.layoutId = :layoutId AND p.x IS NOT NULL AND p.y IS NOT NULL " +
                                "AND p.status IN (:statuses)", Coordinate.class)
                .setParameter("layoutId", layoutId)
                .setParameter("statuses", List.of(PackageStatus.ALLOCATED, PackageStatus.STORED, PackageStatus.SENDING))
                .getResultList();
        return new HashSet<>(results);
    }

    @Override
    public Set<Coordinate> findOccupiedCoordinates() {
        final List<Coordinate> results = this.entityManager.createQuery(
                        "SELECT new io.swiczka.github.sharedcommon.helpers.Coordinate(p.x, p.y) " +
                                "FROM PackageItem p " +
                                "WHERE p.x IS NOT NULL AND p.y IS NOT NULL " +
                                "AND p.status IN (:statuses)", Coordinate.class)
                .setParameter("statuses", List.of(PackageStatus.ALLOCATED, PackageStatus.STORED, PackageStatus.SENDING))
                .getResultList();
        return new HashSet<>(results);
    }

    @Override
    public void delete(final PackageItem packageItem) {
        if (this.entityManager.contains(packageItem)) {
            this.entityManager.remove(packageItem);
            return;
        }
        final PackageItem managed = this.entityManager.find(PackageItem.class, packageItem.getId());
        if (managed != null) {
            this.entityManager.remove(managed);
        }
    }
}
