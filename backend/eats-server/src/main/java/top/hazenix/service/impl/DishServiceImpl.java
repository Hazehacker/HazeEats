package top.hazenix.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.hazenix.constant.MessageConstant;
import top.hazenix.constant.StatusConstant;
import top.hazenix.dto.DishDTO;
import top.hazenix.dto.DishPageQueryDTO;
import top.hazenix.entity.Dish;
import top.hazenix.entity.DishFlavor;
import top.hazenix.exception.DeletionNotAllowedException;
import top.hazenix.mapper.DishMapper;
import top.hazenix.mapper.DishFlavorMapper;
import top.hazenix.mapper.SetmealDishMapper;
import top.hazenix.result.PageResult;
import top.hazenix.service.DishService;
import top.hazenix.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */

    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        // （DTO里面包含了要插入两个表的数据）
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(1);
//        dish.setCreateTime(LocalDateTime.now());
//        dish.setUpdateTime(LocalDateTime.now());
//        dish.setUpdateUser(BaseContext.getCurrentId());
//        dish.setCreateUser(BaseContext.getCurrentId());
        //向菜品表插入数据
        dishMapper.insert(dish);
        //(这个菜品还没创建，前端传过来的数据中（的flavors集合）不包括dishId，所以要后端获取DishId)
        // [这个逻辑在insert入菜品表的时候完成【mybatis主键回填】]

        //获取insert语句生成的主键值
        Long dishId = dish.getId();

        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);//这样在批量插入的时候就有dishId了
//                dishFlavor.setDishId(dish.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }


    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //* 可以一次删除一个，也可以批量删除

        //* 起售中的菜品不能删除
        Boolean isExceptional = false;
        Boolean isRelevantToSet = false;

        // 收集可以删除的菜品ID
        List<Long> idsToDelete = new ArrayList<>();

        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                isExceptional = true;
            } else {
                //* <u>被套餐关联的菜品不能删除</u>
                Long dishSetCount = setmealDishMapper.getSetmealCountByDishId(id);
                if (dishSetCount > 0) {
                    isRelevantToSet = true;
                } else {
                    // 可以安全删除的菜品
                    idsToDelete.add(id);
                }
            }
        }

        // 执行删除操作
        if (!idsToDelete.isEmpty()) {
            //删除菜品数据
            dishMapper.delByIds(idsToDelete);
            //* <u>删除菜品后，关联的口味数据也需要删除掉</u>
            dishFlavorMapper.deleteByDishIds(idsToDelete);
        }

        if (isExceptional && isRelevantToSet) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE + "/n" + MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        if (isExceptional) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        if (isRelevantToSet) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
    }

    @Override
    @Transactional
    public DishVO getById(Long id) {

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dishMapper.getById(id), dishVO);
        dishVO.setFlavors(dishFlavorMapper.selectByDishId(id));
        return dishVO;

    }

    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
//        dish.setUpdateTime(LocalDateTime.now());
//        dish.setUpdateUser(BaseContext.getCurrentId());
        try {
            dishMapper.update(dish);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long dishId = dish.getId();
        dishFlavorMapper.deleteByDishIds(Collections.singletonList(dish.getId()));
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        List<Dish> dishs = dishMapper.list(dish);
        return dishs;
    }

/**
 * 条件查询菜品和口味
 * @param dish
 * @return
 */
//    @Override
    public List<DishVO> listWithFlavor(Dish dish) {


            List<Dish> dishList = dishMapper.list(dish);

            List<DishVO> dishVOList = new ArrayList<>();

            for (Dish d : dishList) {
                DishVO dishVO = new DishVO();
                BeanUtils.copyProperties(d,dishVO);

                //根据菜品id查询对应的口味
                List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(d.getId());

                dishVO.setFlavors(flavors);
                dishVOList.add(dishVO);
            }

            return dishVOList;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                        .status(status)
                                .id(id)
                                        .build();
        dishMapper.update(dish);
    }
}


