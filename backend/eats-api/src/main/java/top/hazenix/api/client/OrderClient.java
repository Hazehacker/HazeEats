package top.hazenix.api.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.hazenix.api.domain.dto.GoodsSalesDTO;
import top.hazenix.api.domain.entity.AddressBook;
import top.hazenix.result.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@FeignClient(name = "order-service")
public interface OrderClient {
    // ---------------------订单相关接口-----------------------------

    /**
     * 支付完成
     * @param
     * @return
     */
    @PutMapping("/user/order/pay-success")
    void paySuccess(@RequestParam String outTradeNo);

    /**
     * 获取指定时间范围内的总营业额（订单价格总和）
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/admin/order/sum")
    Double sumByStatusAndOrderTime(@RequestParam Integer status, @RequestParam LocalDateTime beginTime, @RequestParam LocalDateTime endTime);


    /**
     * 获取指定时间范围内的订单总数/有效订单总数
     * @param beginTime
     * @param endTime
     * @param
     * @return
     */
    @GetMapping("/admin/order/count")
    Integer getCountByDateAndStatus(@RequestParam LocalDateTime beginTime, @RequestParam LocalDateTime endTime, @RequestParam Integer status);


    /**
     * 统计指定时间区间内的销量排名top10
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/admin/order/top10")
    List<GoodsSalesDTO> getSalesTop10(@RequestParam LocalDateTime beginTime, @RequestParam LocalDateTime endTime);

    /**
     * 统计不同订单状态的订单数目
     * @param map
     * @return
     */
    @GetMapping("/admin/order/countByMap")
    public Integer countByMap(@RequestParam Map map);
    // -----------------------------------------------------------
}
