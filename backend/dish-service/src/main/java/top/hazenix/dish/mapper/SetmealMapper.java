package top.hazenix.dish.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.hazenix.dish.annotation.AutoFill;
import top.hazenix.dish.domain.dto.SetmealPageQueryDTO;
import top.hazenix.dish.domain.entity.Setmeal;
import top.hazenix.dish.domain.vo.DishItemVO;
import top.hazenix.dish.domain.vo.SetmealVO;
import top.hazenix.enumeration.OperationType;


import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteBySetmealIds(List<Long> ids);

    Setmeal getById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    List<Setmeal> list(Setmeal setmeal);


    List<DishItemVO> getDishItemBySetmealId(Long id);

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
