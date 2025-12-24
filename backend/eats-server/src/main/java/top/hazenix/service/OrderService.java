package top.hazenix.service;

import top.hazenix.dto.*;
import top.hazenix.result.PageResult;
import top.hazenix.vo.OrderPaymentVO;
import top.hazenix.vo.OrderStatisticsVO;
import top.hazenix.vo.OrderSubmitVO;
import top.hazenix.vo.OrderVO;

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
}
