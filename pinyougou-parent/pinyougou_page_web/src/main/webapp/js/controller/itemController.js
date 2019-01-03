app.controller("itemController",function ($scope,$http) {

	//数量操作
	$scope.addNum = function(x){
		$scope.num = $scope.num+x;
		if($scope.num<1){
			$scope.num =1;
		}
	}
	
	//记录用户选择的规格
	$scope.specificationItems={};
	
	//用户选择规格
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key] = value;
		searchSku();//读取sku
	}
	//判断某规格选项是否被用户选中
	$scope.isSelected = function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}	
		else{
			return false;
		}
	}
	
	$scope.sku={};//当前选择的sku
	
	//加载默认SKU
	$scope.loadSku = function(){
		$scope.sku = skuList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
	}
	
	//匹配两个对象
	matchObject=function(map1,map2){
		for(var key in map1){
			if(map1[key] != map2[key]){
				return false;
			}
		}
		for(var key in map2){
			if(map2[key] != map1[key]){
				return false;
			}
		}
		return true;
	}
	
	//根据规格查询sku
	searchSku= function(){
		for(var i = 0 ; i < skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		$scope.sku={id:0,title:'------',price:0};//如果没有匹配的
	}
	
	//添加商品到购物车
	$scope.addToCart=function(){
		// alert('skuid:'+$scope.sku.id);
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
            + $scope.sku.id +'&num='+$scope.num,{'withCredentials':true}).success(
            	function (response) {
					if (response.success){
                        //跳转到购物车页面
						location.href='http://localhost:9107/cart.html';//跳转到购物车页面
					}else{
                        alert(response.message);
                    }
                }
		);


	}
  
});