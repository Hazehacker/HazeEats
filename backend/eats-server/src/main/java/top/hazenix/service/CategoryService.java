package top.hazenix.service;

import top.hazenix.dto.*;
import top.hazenix.entity.Category;
import top.hazenix.result.PageResult;

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
