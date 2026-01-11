package top.hazenix.order.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.hazenix.api.client.CartClient;
import top.hazenix.api.client.UserClient;
import top.hazenix.api.domain.dto.ShoppingCartDTO;
import top.hazenix.api.domain.entity.AddressBook;
import top.hazenix.api.domain.entity.ShoppingCart;
import top.hazenix.constant.MessageConstant;
import top.hazenix.context.BaseContext;
import top.hazenix.exception.AddressBookBusinessException;
import top.hazenix.exception.OrderBusinessException;
import top.hazenix.exception.ShoppingCartBusinessException;
import top.hazenix.order.domain.dto.*;

import top.hazenix.order.domain.entity.OrderDetail;
import top.hazenix.order.domain.entity.Orders;
import top.hazenix.order.domain.vo.OrderPaymentVO;
import top.hazenix.order.domain.vo.OrderStatisticsVO;
import top.hazenix.order.domain.vo.OrderSubmitVO;
import top.hazenix.order.domain.vo.OrderVO;
import top.hazenix.order.mapper.OrderDetailMapper;
import top.hazenix.order.mapper.OrderMapper;
import top.hazenix.order.service.OrderService;
import top.hazenix.order.socket.WebSocketServer;
import top.hazenix.result.PageResult;

import top.hazenix.order.utils.BaiduGeoUtil;
import top.hazenix.order.utils.WeChatPayUtil;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private CartClient cartClient;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private BaiduGeoUtil baiduGeoUtil;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) throws Exception {

        //处理异常情况，抛出异常
        AddressBook addressBook = userClient.getById(ordersSubmitDTO.getAddressBookId()).getData();
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        String detailAddress = addressBook.getDetail();


        String json = null;
        json = baiduGeoUtil.requestGeoCodingAPI(detailAddress);

        JSONObject jsonResponse = JSONObject.parseObject(json);
        JSONObject result = jsonResponse.getJSONObject("result");
        JSONObject location = result.getJSONObject("location");
        double longtitude = location.getDouble("lng");
        double latitude = location.getDouble("lat");
        //经度纬度保留六位小数
        longtitude = new BigDecimal(longtitude).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        latitude = new BigDecimal(latitude).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();


        String anotherJson = baiduGeoUtil.requestRoutePlanApi(longtitude,latitude);
        JSONObject jsonResponse2 = JSONObject.parseObject(anotherJson);
        JSONObject result2 = jsonResponse2.getJSONObject("result");
        JSONArray routes = result2.getJSONArray("routes");//获取routes数组

        // 获取单个route对象
        JSONObject route = routes.getJSONObject(0);

        // 获取route的distance字段
        Long distance = route.getLong("distance");
        log.info("距离：{}",distance);


        if(distance>7000){
            throw new OrderBusinessException("距离商家过远，超出配送距离");
        }else{
            log.info("并未超出配送范围");
        }

//        ShoppingCart shoppingCart = new ShoppingCart();
//        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = cartClient.list().getData();
        if(list == null || list.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向order插入1条数据
        Orders order = new Orders();
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setUserId(BaseContext.getCurrentId());
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        //从address_book表里面获取收货人和电话
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        order.setDeliveryStatus(1);
        orderMapper.insert(order);


        //向order_detial插入多条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        //前面就用userId获取过购物车列表list
        for(ShoppingCart cart :list){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //清空用户的购物车数据
        cartClient.clean();
        //封装VO对象并返回
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();
        return orderSubmitVO;

    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        // 当前登录用户id
//        Long userId = BaseContext.getCurrentId();
//        User user = userMapper.getById(userId);
//
//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));

        //[][]
        log.info("跳过微信支付，支付成功");
        paySuccess(ordersPaymentDTO.getOrderNumber());
        return new OrderPaymentVO();
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);// 包含了判断和更新的逻辑，状态不是TO_BE_CONFIRMED的订单不会被更新，保证了幂等性

        //通过websocket闲客户端浏览器推送消息
        Map map = new HashMap();
        map.put("type",1);//1表示来电提醒，2表示用户催单
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号："+outTradeNo);
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
        
    }

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
         ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());


        if (ordersPageQueryDTO.getStatus() != null) {
            ordersPageQueryDTO.setStatus(ordersPageQueryDTO.getStatus());
        }
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> list = new ArrayList<>();
        if(page != null && page.getTotal() > 0){
            for(Orders orders : page){
                Long orderId = orders.getId();
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                orderVO.setOrderDetailList(orderDetails);
                list.add(orderVO);
            }
        }

        return new PageResult(page.getTotal(),list);
    }

    @Override
    public void reminder(Long id) {
        //根据id查询订单
        Orders ordersDB = orderMapper.getById(id);
        //校验订单是否存在
        if(ordersDB == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //校验订单状态


        //通过websocket闲客户端浏览器推送消息
        Map map = new HashMap();
        map.put("type",2);//1表示来电提醒，2表示用户催单
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号："+ordersDB.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);




    }

    @Override
    public OrderVO getOrderDetail(Long id) {

        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    public void cancel(Long id) throws Exception {
        //商家已接单状态下，用户取消订单需电话沟通商家
        Orders orders = orderMapper.getById(id);
        // 校验订单是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }



//        if(orders.getStatus() == Orders.TO_BE_CONFIRMED || orders.getStatus() == Orders.DELIVERY_IN_PROGRESS){
//            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
//        }
        //实际上大于2的情况都要抛异常（虽然正常情况已完成和已取消前端没有提供按钮发起请求，但是不排除有人用postman发起请求）
        if(orders.getStatus() > 2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (orders.getPayStatus().equals(Orders.TO_BE_CONFIRMED)){
            orders.setStatus(Orders.CANCELLED);

            //给用户退款
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    orders.getNumber(), //商户订单号
                    orders.getNumber(), //商户退款单号
                    orders.getAmount(),//退款金额，单位 元
                    orders.getAmount());//原订单金额


            orders.setPayStatus(Orders.REFUND);
            orders.setCancelTime(LocalDateTime.now());
            orders.setCancelReason("用户取消订单");
            orderMapper.update(orders);
        }

    }

    @Override
    public void repetition(Long id) {
        Orders orders = orderMapper.getById(id);
        //获取这个订单对应的订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
            shoppingCartDTO.setDishFlavor(orderDetail.getDishFlavor());
            if(orderDetail.getDishId()!=null){
                shoppingCartDTO.setDishId(orderDetail.getDishId());
            }else{
                shoppingCartDTO.setSetmealId(orderDetail.getSetmealId());
            }
            cartClient.addShoppingCart(shoppingCartDTO);
        }
    }

    /**
     * 条件查询
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionalSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        if(page == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //【接口文档看仔细了，里面有一个orderDishes(String)字段也要返回，所以这里要用orderVO】
        List<Orders> ordersList = page.getResult();
        List<OrderVO> list = new ArrayList<>();
        for (Orders orders : ordersList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

//            String orderDishes = StringUtils.join(orderDetailList, ",");
            // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
            List<String> orderDishList = orderDetailList.stream().map(x -> {
                String orderDish = x.getName() + "*" + x.getNumber() + ";";
                return orderDish;
            }).collect(Collectors.toList());
            // 将该订单对应的所有菜品信息拼接在一起
            String orderDishes = String.join("", orderDishList);


            orderVO.setOrderDishes(orderDishes);
            list.add(orderVO);
        }

        return new PageResult(page.getTotal(),list);
    }

    /**
     * 查询不同状态的订单数目
     * @return
     */
    @Override
    public OrderStatisticsVO getOrderStatistics() {
        //获取处于待派送状态的订单数量
        Integer confirmed = orderMapper.countByStatus(3);
        //获取处于派送状态的订单数量
        Integer deliveryInProgress = orderMapper.countByStatus(4);
        //获取处于待接单状态的订单数量
        Integer toBeConfirmed = orderMapper.countByStatus(2);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        Orders orders = orderMapper.getById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());
        String orderDishes = StringUtils.join(orderDishList, ",");
        orderVO.setOrderDishes(orderDishes);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 商家接单
     * @param ordersDTO
     */
    @Override
    public void confirm(OrdersDTO ordersDTO) {

        Orders orders = new Orders();
        orders.setId(ordersDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        Orders orders = new Orders();
        //- 只有订单处于“待接单、待派送”状态时可以执行拒单或取消操作
        Orders originOrder = orderMapper.getById(ordersRejectionDTO.getId());
        if(!originOrder.getStatus().equals(Orders.TO_BE_CONFIRMED)|| orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //- 商家拒单其实就是将订单状态修改为“已取消”
        orders.setId(ordersRejectionDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        //- 商家拒单时需要指定拒单原因
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason(ordersRejectionDTO.getRejectionReason());

        //- 商家拒单时，如果用户已经完成了支付，需要为用户退款(我目前没有商户号，先注释掉)
        if(originOrder.getPayStatus().equals(Orders.PAID)){
//            weChatPayUtil.refund(
//                    originOrder.getNumber(),
//                    originOrder.getNumber(),
//                    originOrder.getAmount(),
//                    originOrder.getAmount()
//            );
            orders.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(orders);


    }

    /**
     * 取消接单过的订单
     * @param ordersCancelDTO
     */
    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        //- 只有订单处于“待接单、待派送”状态时可以执行拒单或取消操作
        Orders originOrder = orderMapper.getById(ordersCancelDTO.getId());
        if(!(originOrder.getStatus().equals(Orders.CONFIRMED))|| orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //- 商家拒单其实就是将订单状态修改为“已取消”
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        //- 商家拒单时需要指定拒单原因
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason(ordersCancelDTO.getCancelReason());

        //- 商家拒单时，如果用户已经完成了支付，需要为用户退款(我目前没有商户号，先注释掉)
        if(originOrder.getPayStatus().equals(Orders.PAID)){
//            weChatPayUtil.refund(
//                    originOrder.getNumber(),
//                    originOrder.getNumber(),
//                    originOrder.getAmount(),
//                    originOrder.getAmount()
//            );
            orders.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void delivery(Long id) {
        Orders orders = orderMapper.getById(id);
        //处理异常状态的订单
        if(!orders.getStatus().equals(Orders.CONFIRMED) || orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);

        //在没有算法的情况下，我先定义成当前时间往后一小时
        orders.setEstimatedDeliveryTime(LocalDateTime.now().plusHours(1));
        orderMapper.update(orders);


    }

    /**
     * 将订单状态修改为已完成
     * @param id
     */
    @Override
    public void complete(Long id) {
        Orders ordersDB = orderMapper.getById(id);
        if(ordersDB == null || ordersDB.getStatus() != Orders.DELIVERY_IN_PROGRESS){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        //deliverytime指的是送达时间，在完成订单这个方法里面填充
        orderMapper.update(orders);

    }

    @Override
    public Double sumOrderCount(Integer status, LocalDateTime beginTime, LocalDateTime endTime) {
        return orderMapper.sumByStatusAndOrderTime(status,beginTime,endTime);
    }

    @Override
    public Integer getCountByDateAndStatus(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        return orderMapper.getCountByDateAndStatus(beginTime,endTime,status);
    }


}
