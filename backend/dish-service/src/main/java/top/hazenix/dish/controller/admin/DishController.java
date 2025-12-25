package top.hazenix.dish.controller.admin;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.hazenix.dish.domain.dto.DishDTO;
import top.hazenix.dish.domain.dto.DishPageQueryDTO;
import top.hazenix.dish.domain.entity.Dish;
import top.hazenix.dish.domain.vo.DishVO;
import top.hazenix.dish.mapper.DishMapper;
import top.hazenix.dish.service.DishService;
import top.hazenix.result.PageResult;
import top.hazenix.result.Result;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private DishMapper dishMapper;


    /**
     * 新增菜品及其口味
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //（此处方便获取categoryId）采用精确删除
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);

        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */

    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询：{}",dishPageQueryDTO);

        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品和对应的口味数据：{}",id);
        DishVO dishVO = dishService.getById(id);

        return Result.success(dishVO);
    }


    /**
     * 修改菜品数据
     * @param dishDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("更新菜品信息：{}",dishDTO);
        dishService.update(dishDTO);
        //清理缓存
        cleanCache("dish_*");
        return Result.success();
    }


    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}",ids);
        dishService.deleteBatch(ids);

        //清理缓存
        cleanCache("dish_*");
        return Result.success();
    }


    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status,Long id){
        dishService.startOrStop(status,id);
        //清理缓存
        cleanCache("dish_*");
        return Result.success();
    }


    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }


    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern ){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    @GetMapping("/countByMap")
    public Integer countDishesByMap(Map map){
        return dishMapper.countByMap(map);
    }

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @GetMapping("/countByCategory")
    public Integer countByCategoryId(Long categoryId){
        return dishMapper.countByCategoryId(categoryId);
    }


}
