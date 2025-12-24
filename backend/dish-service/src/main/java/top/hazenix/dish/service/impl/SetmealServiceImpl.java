package top.hazenix.dish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.hazenix.constant.MessageConstant;
import top.hazenix.constant.StatusConstant;
import top.hazenix.dish.domain.dto.SetmealDTO;
import top.hazenix.dish.domain.dto.SetmealPageQueryDTO;
import top.hazenix.dish.domain.entity.Setmeal;
import top.hazenix.dish.domain.entity.SetmealDish;
import top.hazenix.dish.domain.vo.DishItemVO;
import top.hazenix.dish.domain.vo.SetmealVO;
import top.hazenix.dish.mapper.SetmealDishMapper;
import top.hazenix.dish.mapper.SetmealMapper;
import top.hazenix.dish.service.SetmealService;
import top.hazenix.exception.DeletionNotAllowedException;
import top.hazenix.result.PageResult;


import java.util.Collections;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(1);
        //插入setmeal数据表
        setmealMapper.insert(setmeal);
        //获取插入的setmealId（主键回填）
        Long setmealId = setmeal.getId();

        //插入setmeal_dish中间表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach( setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insertBatch(setmealDishes);

    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 删除单个/批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if(StatusConstant.ENABLE == setmeal.getStatus()){
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        try {
            setmealDishMapper.deleteBySetmealIds(ids);
            setmealMapper.deleteBySetmealIds(ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startOrStop(Integer status,Long id ) {
        Setmeal setmeal = Setmeal.builder()
                        .status(status)
                        .id(id)
                        .build();
        setmealMapper.update(setmeal);
    }

    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishMapper.getSetmealDishesBySetmealId(id));
        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        Long setmealId = setmealDTO.getId();//前端传过来的数据中，SetmealDish集合并不一定包含setmealId，我们需要手动赋值
        setmealDishMapper.deleteBySetmealIds(Collections.singletonList(setmeal.getId()));

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }
    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

}
