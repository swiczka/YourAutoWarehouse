package io.swiczka.github.apiwarehouse.packageitem;

import io.swiczka.github.apiwarehouse.packageitem.response.PackageItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/package")
@Validated
public class PackageItemController {

    private final PackageItemService packageItemService;

    @Autowired
    public PackageItemController(final PackageItemService packageItemService) {
        this.packageItemService = packageItemService;
    }

    @GetMapping("/inbound/{inboundOrderId}")
    public List<PackageItemResponse> getPackagesByInboundOrderId(
            @PathVariable final Long inboundOrderId,
            @RequestHeader("X-Guest-Id") final UUID guestId
    ) {
        return packageItemService.getPackagesByInboundOrderId(inboundOrderId, guestId);
    }

    @GetMapping("/outbound/{outboundOrderId}")
    public List<PackageItemResponse> getPackagesByOutboundOrderId(
            @PathVariable final Long outboundOrderId,
            @RequestHeader("X-Guest-Id") final UUID guestId
    ) {
        return packageItemService.getPackagesByOutboundOrderId(outboundOrderId, guestId);
    }

    @GetMapping("/layout/{layoutId}")
    public List<PackageItemResponse> getPackagesByLayoutId(
            @PathVariable final Long layoutId,
            @RequestHeader("X-Guest-Id") final UUID guestId
    ) {
        return packageItemService.getPackagesByLayoutId(layoutId, guestId);
    }
}
