package top.hazenix.cart.mapper;


import org.apache.ibatis.annotations.Mapper;
import top.hazenix.cart.domain.entity.ShoppingCart;


import java.util.List;

@Mapper
public interface ShoppingCartMapper {


    List<ShoppingCart> list(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);

    void update(ShoppingCart shoppingCart);

    void deleteByUserId(Long currentId);
}
