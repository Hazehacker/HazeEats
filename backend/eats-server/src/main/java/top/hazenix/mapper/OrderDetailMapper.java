package top.hazenix.mapper;



import org.apache.ibatis.annotations.Mapper;
import top.hazenix.entity.OrderDetail;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    void insertBatch(List<OrderDetail> orderDetailList);

    List<OrderDetail> getByOrderId(Long orderId);
}
