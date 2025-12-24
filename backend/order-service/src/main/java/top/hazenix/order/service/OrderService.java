package top.hazenix.order.service;

import top.hazenix.order.domain.dto.*;
import top.hazenix.order.domain.vo.OrderPaymentVO;
import top.hazenix.order.domain.vo.OrderStatisticsVO;
import top.hazenix.order.domain.vo.OrderSubmitVO;
import top.hazenix.order.domain.vo.OrderVO;
import top.hazenix.result.PageResult;

import java.time.LocalDateTime;


public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) throws Exception;

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;
    void paySuccess(String outTradeNo) ;

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    void reminder(Long id);

    OrderVO getOrderDetail(Long id);

    void cancel(Long id) throws Exception;

    void repetition(Long id);

    PageResult conditionalSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO getOrderStatistics();

    OrderVO details(Long id);

    void confirm(OrdersDTO ordersDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    void adminCancel(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void complete(Long id);

    Double sumOrderCount(Integer status, LocalDateTime beginTime, LocalDateTime endTime);

    Integer getCountByDateAndStatus(LocalDateTime beginTime, LocalDateTime endTime, Integer status);
}
