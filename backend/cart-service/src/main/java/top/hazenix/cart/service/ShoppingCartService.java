package top.hazenix.cart.service;



import top.hazenix.cart.domain.dto.ShoppingCartDTO;
import top.hazenix.cart.domain.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list();

    void clean();
}
