package io.swiczka.github.apiwarehouse.layout;

import io.swiczka.github.apiwarehouse.layout.dto.LayoutDTO;
import io.swiczka.github.apiwarehouse.layout.dto.LayoutResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseLayoutController {

    @GetMapping("/")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/new")
    public ResponseEntity<LayoutResponseDTO> setNewLayout(@RequestBody LayoutDTO[] layout){
        LayoutResponseDTO responseDTO = new LayoutResponseDTO("Success", layout.length);
        return ResponseEntity.ok(responseDTO);
    }
}
