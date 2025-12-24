package top.hazenix.controller.user;


import top.hazenix.dto.OrdersPageQueryDTO;
import top.hazenix.dto.OrdersPaymentDTO;
import top.hazenix.dto.OrdersSubmitDTO;
import top.hazenix.result.PageResult;
import top.hazenix.result.Result;
import top.hazenix.service.OrderService;
import top.hazenix.vo.OrderPaymentVO;
import top.hazenix.vo.OrderSubmitVO;
import top.hazenix.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;


    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) throws Exception {
        log.info("用户下单：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }



    /**
     * 历史订单查询
     * @param
     * @return
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> page(Integer page, Integer pageSize, Integer status){
        log.info("查询历史订单（分页查询）");
        OrdersPageQueryDTO ordersPageQueryDTO = OrdersPageQueryDTO.builder()
                .page(page)
                .pageSize(pageSize)
                .status(status)
                .build();
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 根据订单id查询订单详细信息
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id){
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 订单取消
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    public Result<OrderVO> cancel(@PathVariable Long id) throws Exception {
        log.info("取消订单：{}", id);
        orderService.cancel(id);
        return Result.success();
    }


    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单：{}", id);
        orderService.repetition(id);
        return Result.success();
    }


    
    
    /**
     * 客户催单
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable("id") Long id){
        log.info("客户催单：{}", id);
        orderService.reminder(id);
        return Result.success();
    }
    



}
