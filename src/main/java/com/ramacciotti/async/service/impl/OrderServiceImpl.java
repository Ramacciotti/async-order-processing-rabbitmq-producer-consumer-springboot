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
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderProducer orderProducer, OrderRepository orderRepository) {
        this.orderProducer = orderProducer;
        this.orderRepository = orderRepository;
    }

    @Override
    public Long placeOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setProduct(orderDTO.getProduct());
        order.setQuantity(orderDTO.getQuantity());

        order = orderRepository.save(order);
        log.info("Processing new order: {}", order);

        orderProducer.sendOrder(order);
        log.info("Order sent to queue successfully with ID: {}", order.getId());

        return order.getId();
    }

}
