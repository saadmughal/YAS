package com.yas.order.viewmodel.order;

import com.yas.order.model.Order;
import com.yas.order.model.enumeration.*;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record OrderVm(
        Long id,
        String email,
        OrderAddressVm shippingAddressVm,
        OrderAddressVm billingAddressVm,
        String note,
        float tax,
        float discount,
        int numberItem,
        BigDecimal totalPrice,
        BigDecimal deliveryFee,
        String couponCode,
        EOrderStatus orderStatus,
        EDeliveryMethod deliveryMethod,
        EDeliveryStatus deliveryStatus,
        EPaymentStatus paymentStatus,
        EPaymentMethod paymentMethod,
        Set<OrderItemVm> orderItemVms

) {
    public static OrderVm fromModel(Order order) {
        Set<OrderItemVm> orderItemVms = order.getOrderItems().stream().map(
                item -> OrderItemVm.fromModel(item))
                .collect(Collectors.toSet());

        return OrderVm.builder()
                .id(order.getId())
                .email(order.getEmail())
                .shippingAddressVm(OrderAddressVm.fromModel(order.getShippingAddressId()))
                .billingAddressVm(OrderAddressVm.fromModel(order.getBillingAddressId()))
                .note(order.getNote())
                .tax(order.getTax())
                .discount(order.getDiscount())
                .numberItem(order.getNumberItem())
                .totalPrice(order.getTotalPrice())
                .couponCode(order.getCouponCode())
                .orderStatus(order.getOrderStatus())
                .deliveryFee(order.getDeliveryFee())
                .deliveryMethod(order.getDeliveryMethod())
                .deliveryStatus(order.getDeliveryStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .orderItemVms(orderItemVms)
                .build();
    }
}