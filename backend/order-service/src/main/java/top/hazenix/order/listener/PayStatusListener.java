package top.hazenix.order.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import top.hazenix.order.service.OrderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayStatusListener {
    private final OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "order.pay.success.queue",
                    durable = "true"
            ),
            exchange = @Exchange(value = "pay.direct", type = "direct"),
            key = "pay.success"
    ))
    public void paySuccess(Message message) {
        log.info("接收到消息：{}", new String(message.getBody()));
//        String messageId = message.getMessageProperties().getMessageId();
        String orderNo = new String(message.getBody());
        orderService.paySuccess(orderNo);
    }


}
