package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.apiwarehouse.layout.response.LayoutReadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouse")
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
    public LayoutReadResponse getUserLayout(@RequestHeader("X-Guest-Id") final UUID guestId){
        return layoutService.getUserLayout(guestId);
    }

    @PatchMapping("/{id}/forklift")
    public LayoutReadResponse setForkliftNumber(
            @PathVariable Long id,
            @RequestParam int forkliftNumber
          ){
        return layoutService.updateForkliftNumber(id, forkliftNumber);
    }
}
