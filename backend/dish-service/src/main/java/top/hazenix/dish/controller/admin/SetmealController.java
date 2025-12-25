package top.hazenix.dish.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import top.hazenix.dish.domain.dto.SetmealDTO;
import top.hazenix.dish.domain.dto.SetmealPageQueryDTO;
import top.hazenix.dish.domain.vo.SetmealVO;
import top.hazenix.dish.mapper.SetmealMapper;
import top.hazenix.dish.service.SetmealService;

import top.hazenix.result.PageResult;
import top.hazenix.result.Result;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    //清除缓存，你新增了套餐肯定要去删除redis下面的数据啊，要不然user下面展示不就错误了
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}",setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询：{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 批量删除或删除单个套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result del(@RequestParam List<Long> ids){
        setmealService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 修改套餐状态
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("修改套餐状态为：{}",status == 1?"起售":"停售");
        setmealService.startOrStop(status,id);

        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐:{}",id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }


    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    @GetMapping("/countByMap")
    public Integer countByMap(Map map){
        return setmealMapper.countByMap(map);
    }

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @GetMapping("/countByCategoryId")
    public Integer countByCategoryId(Long id){
        return setmealMapper.countByCategoryId(id);
    }





}
