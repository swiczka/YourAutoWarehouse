package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.exceptions.ForbiddenException;
import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
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
    public WarehouseLayoutController(WarehouseLayoutService layoutService) {
        this.layoutService = layoutService;
    }

    @PostMapping("/layout")
    public LayoutReadResponse setNewLayout(@RequestBody List<GridDataDto> layout,
                                           @RequestHeader("X-Guest-Id") UUID guestId){
        return layoutService.saveNewLayout(layout, guestId);
    }

    @GetMapping("/layout")
    public LayoutReadResponse getUserLayout(@RequestHeader("X-Guest-Id") final UUID guestId) {
        return layoutService.getUserLayout(guestId);
    }

    @GetMapping("/layout/{id}")
    public LayoutReadResponse getLayoutById(@PathVariable final Long id,
                                            @RequestHeader("X-Guest-Id") final UUID guestId) {
        final LayoutReadResponse layout = layoutService.getLayoutById(id);
        final boolean isOwner = layout.userId().equals(guestId);
        if (!isOwner)
            throw new ForbiddenException("Access denied to layout with id " + id);

        return layout;
    }

    @PatchMapping("/{id}/forklift")
    public LayoutReadResponse setForkliftNumber(
            @PathVariable Long id,
            @RequestParam
                @Min(value = 1, message = "Forklift number must be between 1 and 12")
                @Max(value = 12, message = "Forklift number must be between 1 and 12")
            int forkliftNumber
          ){
        return layoutService.updateForkliftNumber(id, forkliftNumber);
    }
}
