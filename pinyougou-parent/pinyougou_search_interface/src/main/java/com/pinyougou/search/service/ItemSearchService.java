package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    public Map<String,Object> search(Map searchMap);
    /**
      *@description: 导入数据 
      *@author: FenG
      *@param:
      *@return:void
      *@createTime:  
      */
    public void importList(List list);
    public void deleteByGoodsIds(List goodsIdList);
}
