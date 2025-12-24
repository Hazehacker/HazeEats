package top.hazenix.dish.service;



import top.hazenix.dish.domain.dto.SetmealDTO;
import top.hazenix.dish.domain.dto.SetmealPageQueryDTO;
import top.hazenix.dish.domain.entity.Setmeal;
import top.hazenix.dish.domain.vo.DishItemVO;
import top.hazenix.dish.domain.vo.SetmealVO;
import top.hazenix.result.PageResult;

import java.util.List;

public interface SetmealService {
    void saveWithDish(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteBatch(List<Long> ids);

    void startOrStop(Integer status,Long id);

    SetmealVO getById(Long id);

    void update(SetmealDTO setmealDTO);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
