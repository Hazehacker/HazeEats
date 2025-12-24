package top.hazenix.task;


import top.hazenix.entity.Orders;
import top.hazenix.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
    public void proccessTimeoutOrders(){
        log.info("定时处理超时订单", LocalDateTime.now());
        //查询处于代付款状态并且超过5分钟的订单(order_time<当前时间-5min)
        List<Orders> list = orderMapper.getByStatusAndOrderTimeOut(Orders.PENDING_PAYMENT,LocalDateTime.now().plusMinutes(-5));
        if(list != null && list.size() > 0){
            for(Orders orders :list){
                //注意要设置的字段有三个
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
            }
        }
        orderMapper.updateBatch(list);


    }

    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨1点触发
    public void processDeliveryOrder(){
        log.info("定时处理处于派送中的订单：{}",LocalDateTime.now());
        List<Orders> list = orderMapper.getByStatusAndOrderTimeOut(Orders.DELIVERY_IN_PROGRESS,LocalDateTime.now().plusMinutes(-60));
        if(list != null && list.size() > 0){
            for(Orders orders :list){
                //注意要设置的字段有三个
                orders.setStatus(Orders.COMPLETED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
            }
        }
        orderMapper.updateBatch(list);
    }
}
