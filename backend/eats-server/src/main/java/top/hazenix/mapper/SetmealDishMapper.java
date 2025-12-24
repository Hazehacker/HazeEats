package top.hazenix.mapper;


import top.hazenix.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询这个菜品对应被收入的套餐数目
     * @param id
     * @return
     */
    Long getSetmealCountByDishId(Long id);

    /**
     * 插入套餐菜品数据(setmeal和dish的中间表)
     * @param setmealDish
     */
    void insert(SetmealDish setmealDish);

    void deleteBySetmealIds(List<Long> ids);

    void insertBatch(List<SetmealDish> setmealDishes);

    List<SetmealDish> getSetmealDishesBySetmealId(Long id);
}
