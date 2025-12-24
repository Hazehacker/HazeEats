package top.hazenix.service;

import top.hazenix.dto.ShoppingCartDTO;
import top.hazenix.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list();

    void clean();
}
