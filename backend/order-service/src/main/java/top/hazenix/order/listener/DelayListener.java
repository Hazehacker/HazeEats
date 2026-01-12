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
        // 检查订单是否状态
        // 如果未支付，则取消订单
        String orderId = new String(message.getBody());
        Orders order = orderMapper.getById(Long.valueOf(orderId));
        if(order.getPayStatus() == Orders.UN_PAID){
            orderService.cancel(order.getId());
        }

    }
}
