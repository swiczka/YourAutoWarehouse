package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.apiwarehouse.entity.PackageItem;
import io.swiczka.github.sharedcommon.helpers.Coordinate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PackageItemDAO {
    void save(final PackageItem packageItem);
    Optional<PackageItem> findById(final Long id);
    List<PackageItem> findByLayoutId(final Long layoutId);
    List<PackageItem> findByInboundOrderId(final Long inboundOrderId);
    List<PackageItem> findByOutboundOrderId(final Long outboundOrderId);
    List<PackageItem> findByLayoutIdAndInboundOrderId(final Long layoutId, final Long inboundOrderId);
    List<PackageItem> findByLayoutIdAndOutboundOrderId(final Long layoutId, final Long outboundOrderId);
    Optional<PackageItem> findByCoordinates(final Long layoutId, final int x, final int y);
    Optional<PackageItem> findByCoordinates(final int x, final int y);
    Set<Coordinate> findOccupiedCoordinates(final Long layoutId);
    Set<Coordinate> findOccupiedCoordinates();
    void delete(final PackageItem packageItem);
}
