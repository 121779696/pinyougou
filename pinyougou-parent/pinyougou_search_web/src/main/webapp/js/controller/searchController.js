app.controller("searchController",function ($scope,$location,searchService) {

    //搜索对象
    $scope.searchMap={'keywords':'' , 'category':'' , 'brand':'' , 'spec':{} , 'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};

    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;

                buildPageLabel()
            }
        );
    }
    //构建分页栏
    buildPageLabel = function () {
        $scope.pageLabel=[]; //新增分页栏属性
        var firstPage=1;
        var lastPage=$scope.resultMap.totalPages;
        $scope.firstDot= true;
        $scope.lastDot= true;
        if ($scope.resultMap.totalPages > 5){  //如果总页数大于5页,显示部分页码
            if ($scope.searchMap.pageNo <= 3){  //如果当前页小于等于3
                lastPage = 5;
                $scope.firstDot = false; //前面无点
            }else if ($scope.searchMap.pageNo >=$scope.resultMap.totalPages - 2){  //如果当前页大于等于最大页码-2
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot=false; //后边无点
            }else{  //显示当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        }else {  //如果总页数小于5页,全部显示
            $scope.firstDot = false; //前面无点
            $scope.lastDot = false; //后边无点
        }
        //循环产生页码标签
        for (var i = firstPage; i <=lastPage; i++) {
            $scope.pageLabel.push(i);
        }

    }
    //根据页码查询
    $scope.queryByPage=function (pageNo) {
        if (pageNo <1 || pageNo > $scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    }
    //判断当前页为第一页
    $scope.isTopPage=function () {
        if ($scope.searchMap.pageNo == 1){
            return true;
        }else{
            return false;
        }
    }
    //判断当前页是否未最后一页
    $scope.isEndPage=function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }
    }

    $scope.addSearchItem=function (key,value) {
        if (key=='category' || key =='brand' || key == 'price'){
            $scope.searchMap[key] = value;
        }
        else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }
    $scope.removeSearchItem=function (key) {
        if (key=='category' || key =='brand' || key == 'price'){
            $scope.searchMap[key] = '';
        }
        else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
    //设置排序规则
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }
    //判断关键字是不是品牌
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }
    //加载查询字符串
    $scope.loadkeywords=function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }

});