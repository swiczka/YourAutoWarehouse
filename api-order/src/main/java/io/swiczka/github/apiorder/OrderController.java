package io.swiczka.github.apiorder;

import io.swiczka.github.apiorder.dto.InboundOrderCreateDto;
import io.swiczka.github.apiorder.dto.OutboundOrderCreateDto;
import io.swiczka.github.apiorder.response.InboundOrderResponse;
import io.swiczka.github.apiorder.response.OutboundOrderResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/inbound")
    public InboundOrderResponse addNewInboundOrder(
            @Valid @RequestBody final InboundOrderCreateDto dto,
            @RequestHeader("X-Guest-Id") final UUID guestId) {
        return orderService.addInboundOrder(dto, guestId);
    }

    @GetMapping("/inbound")
    public List<InboundOrderResponse> getInboundByUser(@RequestHeader("X-Guest-Id") final UUID guestId) {
        return orderService.getInboundByUser(guestId);
    }

    @PostMapping("/outbound")
    public OutboundOrderResponse addNewOutboundOrder(
            @Valid @RequestBody final OutboundOrderCreateDto dto,
            @RequestHeader("X-Guest-Id") final UUID guestId) {
        return orderService.addOutboundOrder(dto, guestId);
    }

    @GetMapping("/outbound")
    public List<OutboundOrderResponse> getOutboundByUser(@RequestHeader("X-Guest-Id") final UUID guestId) {
        return orderService.getOutboundByUser(guestId);
    }
}
