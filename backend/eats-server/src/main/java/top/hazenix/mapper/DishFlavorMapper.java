package top.hazenix.mapper;


import top.hazenix.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    void insertBatch(List<DishFlavor> flavors);


    void deleteByDishIds(List<Long> dishIds);

    List<DishFlavor> selectByDishId(Long id);


}
