package top.hazenix.controller.user;


import top.hazenix.dto.ShoppingCartDTO;
import top.hazenix.entity.ShoppingCart;
import top.hazenix.result.Result;
import top.hazenix.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
