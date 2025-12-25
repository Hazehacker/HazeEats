package top.hazenix.shop.service;



import top.hazenix.result.PageResult;
import top.hazenix.shop.domain.dto.CategoryDTO;
import top.hazenix.shop.domain.dto.CategoryPageQueryDTO;
import top.hazenix.shop.domain.entity.Category;

import java.util.List;

public interface CategoryService {

    void add(CategoryDTO categoryDTO);

    void changeStatus(Integer status, Long id);

    void update(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);


    Category selectByType(Integer type);

    void del(Long id);

    List<Category> list(Integer type);
}
