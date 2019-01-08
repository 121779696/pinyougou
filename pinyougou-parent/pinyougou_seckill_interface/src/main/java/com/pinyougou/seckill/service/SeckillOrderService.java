package com.pinyougou.seckill.service;

import java.util.List;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);

	/**
	 * @description 秒杀下单
	 * @param
	 * @return void
	 */
	public void submitOrder(Long seckillId,String userId);
	
	/**
	 * @description 根据用户名查询秒杀订单`
	 * @param userId
	 * @return com.pinyougou.pojo.TbSeckillOrder
	 */
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId);

	/**
	 * @description 支付成功保存订单
	 * @param userId
	 * @param orderId
	 * @param transactionId
	 * @return void
	 */
	public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);
}