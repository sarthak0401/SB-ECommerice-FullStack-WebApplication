package org.ecommerce.project.controller;

import org.ecommerce.project.idempotency.service.IdempotencyService;
import org.ecommerce.project.payload.OrderRequestDTO;
import org.ecommerce.project.service.OrderService;
import org.ecommerce.project.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private IdempotencyService idempotencyService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<?> orderProduct(
            @PathVariable String paymentMethod,
            @RequestBody OrderRequestDTO orderRequestDTO,
            @RequestHeader("Idempotency-key") String key
            ){
        String emailId_Of_loggedInUser = authUtils.loggedInUserEmail();
//        OrderDTO orderInfo =  orderService.placeOrder(emailId_Of_loggedInUser,paymentMethod, orderRequestDTO);

        return idempotencyService.execute(key, ()-> ResponseEntity.ok(orderService.placeOrder(emailId_Of_loggedInUser, paymentMethod, orderRequestDTO)));
    }
}
