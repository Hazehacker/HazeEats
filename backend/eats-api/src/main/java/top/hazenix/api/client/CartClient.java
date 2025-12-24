package top.hazenix.api.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import top.hazenix.api.domain.dto.ShoppingCartDTO;
import top.hazenix.api.domain.entity.AddressBook;
import top.hazenix.api.domain.entity.ShoppingCart;
import top.hazenix.result.Result;

import java.util.List;

@FeignClient(name = "cart-service")
public interface CartClient {
    // ---------------------购物车相关接口-----------------------------

    /**
     * 返回购物车数据
     * @return
     */
    @GetMapping("user/shoppingCart/list")
    public Result<List<ShoppingCart>> list();

    /**
     * 清空当前用户购物车
     * @param
     * @return
     */
    @DeleteMapping("/user/shoppingCart/clean")
    public Result clean();

    /**
     * 新增购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO);

    // -----------------------------------------------------------
}
