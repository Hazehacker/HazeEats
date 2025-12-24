package top.hazenix.mapper;


import top.hazenix.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {


    List<ShoppingCart> list(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);

    void update(ShoppingCart shoppingCart);

    void deleteByUserId(Long currentId);
}
