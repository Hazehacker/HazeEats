package top.hazenix.api.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import top.hazenix.api.domain.vo.DishVO;
import top.hazenix.api.domain.vo.SetmealVO;
import top.hazenix.result.Result;

import java.util.Collection;
import java.util.Map;

@FeignClient(name = "dish-service")
public interface DishClient {
    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @GetMapping("/admin/dish/{id}")
    public Result<DishVO> getById(@PathVariable Long id);
    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/admin/setmeal/{id}")
    public Result<SetmealVO> getBSetmealById(@PathVariable Long id);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    @GetMapping("/admin/dish/countByMap")
    public Integer countDishesByMap(@RequestParam Map map);

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    @GetMapping("/admin/setmeal/countByMap")
    public Integer countSetmealsByMap(@RequestParam Map map);

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @GetMapping("/admin/dish/countByCategory")
    public Integer countDishesByCategoryId(@RequestParam Long categoryId);


    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @GetMapping("/admin/setmeal/countByCategoryId")
    public Integer countSetmealsByCategoryId(Long id);
}
