package top.hazenix.service;

import top.hazenix.dto.DishDTO;
import top.hazenix.dto.DishPageQueryDTO;
import top.hazenix.entity.Dish;
import top.hazenix.result.PageResult;
import top.hazenix.vo.DishVO;

import java.util.List;

public interface DishService {

    void saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);



    void deleteBatch(List<Long> ids);

    DishVO getById(Long id);

    void update(DishDTO dishDTO);

    List<Dish> list(Long categoryId);
    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

    void startOrStop(Integer status, Long id);
}
