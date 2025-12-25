package top.hazenix.shop.service.impl;

import top.hazenix.shop.domain.entity.Category;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hazenix.api.client.DishClient;
import top.hazenix.constant.MessageConstant;
import top.hazenix.context.BaseContext;
import top.hazenix.exception.DeletionNotAllowedException;
import top.hazenix.result.PageResult;
import top.hazenix.shop.domain.dto.CategoryDTO;
import top.hazenix.shop.domain.dto.CategoryPageQueryDTO;
import top.hazenix.shop.mapper.CategoryMapper;
import top.hazenix.shop.service.CategoryService;


import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishClient dishClient;


    @Override
    public void add(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(0);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Long id = BaseContext.getCurrentId();
        category.setCreateUser(id);
        category.setUpdateUser(id);
        categoryMapper.add(category);
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);

    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);



    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> records = page.getResult();
        return new PageResult(total,records);

    }

    @Override
    public Category selectByType(Integer type) {
        Category category = categoryMapper.selectByType(type);
        return category;
    }

    @Override
    public void del(Long id) {

        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = dishClient.countDishesByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = dishClient.countSetmealsByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除分类数据
        categoryMapper.del(id);

    }

    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}
