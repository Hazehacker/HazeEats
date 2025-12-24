package top.hazenix.dish.service;


import top.hazenix.dish.domain.dto.DishDTO;
import top.hazenix.dish.domain.dto.DishPageQueryDTO;
import top.hazenix.dish.domain.entity.Dish;
import top.hazenix.dish.domain.vo.DishVO;
import top.hazenix.result.PageResult;

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
