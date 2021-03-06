package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAll();

    PageResult findPage(int pageNum,int pageSize);

    public void add(TbBrand brand);

    public TbBrand findOne(Long id);

    public void  update(TbBrand brand);

    public void delete(Long[] ids);

    public PageResult findPage(TbBrand brand,int pageNum,int pageSize);

    List<Map> selectOptionList();
}
