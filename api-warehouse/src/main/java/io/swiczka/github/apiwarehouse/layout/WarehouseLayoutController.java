package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.apiwarehouse.layout.response.LayoutIdReadResponse;
import io.swiczka.github.sharedcommon.dto.LayoutOwnerDto;
import io.swiczka.github.sharedcommon.dto.WarehouseLayoutDto;
import io.swiczka.github.apiwarehouse.layout.response.LayoutReadResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouse")
@Validated
public class WarehouseLayoutController {

    private final WarehouseLayoutService layoutService;

    @Autowired
    public WarehouseLayoutController(final WarehouseLayoutService layoutService) {
        this.layoutService = layoutService;
    }

    @PostMapping("/layout")
    public LayoutReadResponse setNewLayout(
            @RequestBody final List<GridDataDto> layout,
            @RequestHeader("X-Guest-Id") final UUID guestId
    ) {
        return layoutService.saveNewLayout(layout, guestId);
    }

    @GetMapping("/layout/latest")
    public LayoutReadResponse getLatestUserLayout(@RequestHeader("X-Guest-Id") final UUID guestId) {
        return layoutService.getLatestUserLayout(guestId);
    }

    @GetMapping("/layouts")
    public List<LayoutIdReadResponse> getUserLayoutIds(@RequestHeader("X-Guest-Id") final UUID guestId) {
        return layoutService.getUserLayoutIds(guestId);
    }

    @GetMapping("/layout/{id}/owner")
    public LayoutOwnerDto getLayoutOwner(@PathVariable final Long id) {
        return layoutService.getLayoutOwner(id);
    }

    @GetMapping("/layout/{id}")
    public LayoutReadResponse getLayoutById(
            @PathVariable final Long id,
            @RequestHeader("X-Guest-Id") final UUID guestId
    ) {
        return layoutService.getLayoutById(id, guestId);
    }

    @GetMapping("/layout/{id}/internal")
    public WarehouseLayoutDto getLayoutByIdInternal(@PathVariable final Long id) {
        return layoutService.getLayoutByIdInternal(id);
    }

    @PatchMapping("/layout/{id}/forklift")
    public LayoutReadResponse setForkliftNumber(
            @PathVariable final Long id,
            @RequestParam
                @Min(value = 1, message = "Forklift number must be between 1 and 12")
                @Max(value = 12, message = "Forklift number must be between 1 and 12")
            final int forkliftNumber
    ) {
        return layoutService.updateForkliftNumber(id, forkliftNumber);
    }
}
