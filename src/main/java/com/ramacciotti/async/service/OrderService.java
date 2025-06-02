package com.ramacciotti.async.service;

import com.ramacciotti.async.dto.OrderDTO;

public interface OrderService {

    Long placeOrder(OrderDTO orderDTO);

}
