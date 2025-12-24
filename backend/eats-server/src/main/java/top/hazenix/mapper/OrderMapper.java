package top.hazenix.mapper;


import com.github.pagehelper.Page;
import top.hazenix.dto.GoodsSalesDTO;
import top.hazenix.dto.OrdersPageQueryDTO;
import top.hazenix.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    List<Orders> getByStatusAndOrderTimeOut(Integer pendingPayment, LocalDateTime limitTime);

    void updateBatch(List<Orders> list);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    Orders getById(Long id);

    /**
     * 获取指定时间范围内的总营业额（订单价格总和）
     * @param status
     * @param beginTime
     * @param endTime
     * @return
     */
    Double sumByStatusAndOrderTime(Integer status, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取指定时间范围内的订单总数/有效订单总数
     * @param beginTime
     * @param endTime
     * @param
     * @return
     */
    Integer getCountByDateAndStatus(LocalDateTime beginTime, LocalDateTime endTime,Integer status);


    /**
     * 统计指定时间区间内的销量排名top10
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select count(id) from orders where status = 3")
    Integer getConfirmedCount();
    @Select("select count(id) from orders where status = 4")
    Integer getDeliveryInProgressCount();
    @Select("select count(id) from orders where status = 2")
    Integer getToBeConfirmedCount();

    /**
     * 统计不同订单状态的订单数目
     * @param status
     * @return
     */
    Integer countByStatus(int status);


    /**
     * 统计不同订单状态的订单数目
     * @param map
     * @return
     */
    Integer countByMap(Map map);


}
