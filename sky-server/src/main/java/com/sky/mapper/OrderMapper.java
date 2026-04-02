package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber} and user_id = #{userId}")
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);
    @Update("update orders set status = #{toBeConfirmed}, pay_status = #{paid},  checkout_time = #{checkoutTime} where number = #{orderNumber}")
    void updateStatus(Integer toBeConfirmed, Integer paid, LocalDateTime checkoutTime, String orderNumber);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);
}
