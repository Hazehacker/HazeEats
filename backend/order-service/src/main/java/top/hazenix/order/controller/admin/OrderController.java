package top.hazenix.order.controller.admin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import top.hazenix.api.domain.dto.GoodsSalesDTO;
import top.hazenix.order.domain.dto.OrdersCancelDTO;
import top.hazenix.order.domain.dto.OrdersDTO;
import top.hazenix.order.domain.dto.OrdersPageQueryDTO;
import top.hazenix.order.domain.dto.OrdersRejectionDTO;
import top.hazenix.order.domain.vo.OrderStatisticsVO;
import top.hazenix.order.domain.vo.OrderVO;
import top.hazenix.order.mapper.OrderMapper;
import top.hazenix.order.service.OrderService;
import top.hazenix.result.PageResult;
import top.hazenix.result.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
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


    /**
     * 获取指定时间范围内的总营业额（订单价格总和）
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/sum")
    public Double sumByStatusAndOrderTime(Integer status, LocalDateTime beginTime, LocalDateTime endTime){
        return orderService.sumOrderCount(status,beginTime,endTime);
    }

    /**
     * 获取指定时间范围内的订单总数/有效订单总数
     * @param beginTime
     * @param endTime
     * @param
     * @return
     */
    @GetMapping("/count")
    public Integer getCountByDateAndStatus(LocalDateTime beginTime, LocalDateTime endTime,Integer status){
        return orderService.getCountByDateAndStatus(beginTime, endTime, status);
    }

    /**
     * 统计指定时间区间内的销量排名top10
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/top10")
    public List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime){
        return orderMapper.getSalesTop10(beginTime,endTime);
    }

    /**
     * 统计不同订单状态的订单数目
     * @param map
     * @return
     */
    @GetMapping("/countByMap")
    public Integer countByMap(Map map){
        return orderMapper.countByMap(map);
    }






}
