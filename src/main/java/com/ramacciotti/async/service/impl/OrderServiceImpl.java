package com.ramacciotti.async.service.impl;

import com.ramacciotti.async.dto.OrderDTO;
import com.ramacciotti.async.model.Order;
import com.ramacciotti.async.producer.OrderProducer;
import com.ramacciotti.async.repository.OrderRepository;
import com.ramacciotti.async.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderProducer orderProducer;

    public OrderServiceImpl(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @Override
    public Long placeOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setProduct(orderDTO.getProduct());
        order.setQuantity(orderDTO.getQuantity());

        orderProducer.sendOrder(order);
        log.info("Order sent to queue successfully: {}", order);

        return order.getId();
    }

}
