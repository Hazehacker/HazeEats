package top.hazenix.controller.admin;


import top.hazenix.dto.OrdersCancelDTO;
import top.hazenix.dto.OrdersDTO;
import top.hazenix.dto.OrdersPageQueryDTO;
import top.hazenix.dto.OrdersRejectionDTO;
import top.hazenix.result.PageResult;
import top.hazenix.result.Result;
import top.hazenix.service.OrderService;

import top.hazenix.vo.OrderStatisticsVO;
import top.hazenix.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 条件查询
     * @param beginTime
     * @param endTime
     * @param number
     * @param page
     * @param pageSize
     * @param phone
     * @param status
     * @return
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch
            (LocalDateTime beginTime,
             LocalDateTime endTime,
             String number,Integer page,Integer pageSize,
             String phone,Integer status){
        log.info("条件分页查询");
        OrdersPageQueryDTO ordersPageQueryDTO = OrdersPageQueryDTO.builder()
                .page(page)
                .pageSize(pageSize)
                .status(status)
                .beginTime(beginTime)
                .endTime(endTime)
                .number(number)
                .phone(phone)
                .build();
        PageResult pageResult = orderService.conditionalSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询不同状态的订单数目
     * @return
     */
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.getOrderStatistics();
        return Result.success(orderStatisticsVO);

    }

    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id){
        OrderVO ordervo = orderService.details(id);
        return Result.success(ordervo);
    }

    /**
     * 商家接单
     * @param ordersDTO
     * @return
     */
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersDTO ordersDTO){
        orderService.confirm(ordersDTO);
        return Result.success();
    }

    /**
     * 商家拒单
     * @param
     * @return
     */
    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消接单过的订单
     * @param ordersCancelDTO
     * @return
     * @throws Exception
     */
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        orderService.adminCancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 将订单状态改为已完成
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }






}
