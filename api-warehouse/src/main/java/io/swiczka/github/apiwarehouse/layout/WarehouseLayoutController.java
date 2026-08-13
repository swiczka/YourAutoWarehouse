package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.layout.dto.GridDataDto;
import io.swiczka.github.apiwarehouse.layout.dto.LayoutResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<LayoutResponseDto> setNewLayout(@RequestBody List<GridDataDto> layout){
        layoutService.saveNewLayout(layout);
        LayoutResponseDto responseDTO = new LayoutResponseDto("Success", layout.size());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/layout")
    public ResponseEntity<List<GridDataDto>> getUserLayout(@RequestParam UUID userId){
        List<GridDataDto> gridData = layoutService.getUserLayout(userId);
        ResponseEntity<List<GridDataDto>> resp = ResponseEntity.ok(gridData);
        return resp;
    }
}
