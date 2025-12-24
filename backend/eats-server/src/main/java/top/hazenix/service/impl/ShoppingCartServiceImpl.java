package top.hazenix.service.impl;

import top.hazenix.context.BaseContext;
import top.hazenix.dto.ShoppingCartDTO;
import top.hazenix.entity.Dish;
import top.hazenix.entity.Setmeal;
import top.hazenix.entity.ShoppingCart;
import top.hazenix.mapper.DishMapper;
import top.hazenix.mapper.SetmealMapper;
import top.hazenix.mapper.ShoppingCartMapper;
import top.hazenix.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService
{

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //判断要新增的商品是否已经添加过了
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果添加过了，就数量+1
        if(list != null && list.size()>0){
            shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.update(shoppingCart);

            return ;
        }
        //如果没添加过，就执行插入操作
        shoppingCart.setNumber(1);//第一次添加，数量为1
        shoppingCart.setCreateTime(LocalDateTime.now());
        //【由于属性填充不足，我们还需要商品名称、价格、图像路径这几个参数，所以需要查询一次】
        Long dishId = shoppingCart.getDishId();
        if(dishId != null){
            //说明此时要添加的是菜品
            Dish dish = dishMapper.getById(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());

        }else{
            //说明此时要添加的是套餐
            Long setmealId = shoppingCart.getSetmealId();
            Setmeal setmeal = setmealMapper.getById(setmealId);
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());


        }
        log.info("shoppingCartMapper = {}", shoppingCartMapper);
        shoppingCartMapper.insert(shoppingCart);


    }

    /**
     * 查询购物车列表
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    @Override
    public void clean() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());

        return ;

    }


}
