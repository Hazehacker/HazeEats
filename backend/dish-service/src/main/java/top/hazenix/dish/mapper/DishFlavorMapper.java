package top.hazenix.dish.mapper;


import org.apache.ibatis.annotations.Mapper;
import top.hazenix.dish.domain.entity.DishFlavor;


import java.util.List;

@Mapper
public interface DishFlavorMapper {


    void insertBatch(List<DishFlavor> flavors);


    void deleteByDishIds(List<Long> dishIds);

    List<DishFlavor> selectByDishId(Long id);


}
