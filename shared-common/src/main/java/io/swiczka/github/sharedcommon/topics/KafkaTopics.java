package io.swiczka.github.sharedcommon.topics;

public final class KafkaTopics {
    public static final String LAYOUT_SAVED = "warehouse.layout-saved";
    public static final String INBOUND_ORDER_CREATED = "order.inbound-order-created";
    public static final String FORKLIFT_LOCATION_UPDATED = "forklift.location-updated";
    public static final String PACKAGE_ALLOCATED = "warehouse.package-allocated";
    public static final String PACKAGE_STORED = "forklift.package-stored";
    public static final String PACKAGE_PICKED = "forklift.package-picked";
    private KafkaTopics(){}
}
