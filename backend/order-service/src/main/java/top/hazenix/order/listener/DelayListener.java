package top.hazenix.order.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.hazenix.constant.MessageConstant;
import top.hazenix.order.domain.entity.Orders;
import top.hazenix.order.mapper.OrderMapper;
import top.hazenix.order.service.OrderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DelayListener {
    private final OrderService orderService;
    private final OrderMapper orderMapper;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MessageConstant.ORDER_DELAY_QUEUE, durable = "true"),
            exchange = @Exchange(name = MessageConstant.DELAY_EXCHANGE, delayed = "true"),
            key = MessageConstant.ORDER_DELAY_ROUTING_KEY
    ))
    public void listenDelayMessage(Message message) throws Exception {
        log.info("接收到delay.queue的延迟消息：{}", new String(message.getBody()));
        String orderId = new String(message.getBody());
        Orders order = orderMapper.getById(Long.valueOf(orderId));
        // 前置校验
        if (order == null || order.getStatus() != Orders.PENDING_PAYMENT){
            // 订单不存在 / 已经支付，无需再处理
            return;
        }
        // 订单表支付状态为未支付，接下来查询支付流水状态(支付订单表如果有)
        // 如果有支付订单这张表，先去支付订单表查询 该订单支付状态
        // 如果支付状态为已支付，就将订单表的状态也改为已支付（可能是因为消息没发送到导致没同步）
        // 如果支付状态为未支付，则取消订单

        // 订单存在，如果未支付，则取消订单(恢复库存 (如果有库存这个逻辑))
        if(order.getPayStatus() == Orders.UN_PAID){
            orderService.cancel(order.getId());
        }

    }
}
