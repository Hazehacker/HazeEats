package top.hazenix.controller.admin;


import top.hazenix.dto.CategoryDTO;
import top.hazenix.dto.CategoryPageQueryDTO;
import top.hazenix.entity.Category;
import top.hazenix.result.PageResult;
import top.hazenix.result.Result;
import top.hazenix.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */




@RestController
@Slf4j
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增种类
     * @return
     */
    @PostMapping
    public Result add(@RequestBody CategoryDTO categoryDTO){
        log.info("新增种类：{}",categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success();
    }

    /**
     * 修改状态
     * @param status
     * @param id
     * @return
     */

    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status,Long id){
        log.info("启用/禁用：{}，{}",status,id);
        categoryService.changeStatus(status,id);
        return Result.success();
    }


    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("更新种类信息:{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){

//        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
//        return Result.success(pageResult);
//

        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 根据类型查询种类
     * @param type
     * @return
     */
//    @GetMapping("/list")
//    public Result<Category> selectByType(Integer type){
//        Category category  = categoryService.selectByType(type);
//        return Result.success(category);
//    }
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);

    }






    /**
     *
     */
    @DeleteMapping
    public Result del(Long id){
        categoryService.del(id);

        return Result.success();

    }



}
