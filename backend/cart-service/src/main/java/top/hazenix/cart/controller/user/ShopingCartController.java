package top.hazenix.cart.controller.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import top.hazenix.cart.domain.dto.ShoppingCartDTO;
import top.hazenix.cart.domain.entity.ShoppingCart;
import top.hazenix.cart.service.ShoppingCartService;
import top.hazenix.result.Result;


import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShopingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;



    /**
     * 新增购物车
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("新增购物车",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);

        return Result.success();
    }

    /**
     * 返回购物车数据
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){

        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }


    @DeleteMapping("/clean")
    public Result clean(){
        shoppingCartService.clean();
        return Result.success();
    }


}
