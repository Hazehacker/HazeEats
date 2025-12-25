package top.hazenix.shop.mapper;


import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import top.hazenix.shop.domain.dto.CategoryPageQueryDTO;
import top.hazenix.shop.domain.entity.Category;


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
