package com.ramacciotti.async.controller;

import com.ramacciotti.async.dto.OrderDTO;
import com.ramacciotti.async.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Long placeOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return orderService.placeOrder(orderDTO);
    }
}
