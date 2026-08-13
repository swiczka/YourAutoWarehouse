package io.swiczka.github.apiorder;

import io.swiczka.github.apiorder.dto.InboundOrderCreateDto;
import io.swiczka.github.apiorder.dto.InboundOrderReadDto;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/inbound")
    public ResponseEntity<InboundOrderResponse> addNewInboundOrder(@Valid @RequestBody InboundOrderCreateDto dto){
        InboundOrderResponse resp = orderService.addInboundOrder(dto);
        URI location = URI.create("/api/order/inbound/" + resp.id());
        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping("/inbound")
    public List<InboundOrderReadDto> getInboundByUser(@RequestParam UUID userId){
        List<InboundOrderReadDto> orders = this.orderService.getInboundByUser(userId);
        return orders;
    }
}
