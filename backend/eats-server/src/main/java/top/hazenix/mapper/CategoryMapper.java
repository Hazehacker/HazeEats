package top.hazenix.mapper;


import com.github.pagehelper.Page;
import top.hazenix.dto.CategoryPageQueryDTO;
import top.hazenix.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {


    public void add(Category category) ;

    void update(Category category);

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    Category selectByType(Integer type);

    void del(Long id);

    List<Category> list(Integer type);
}
