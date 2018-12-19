app.controller("baseController",function ($scope) {
    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,  //当前页面
        totalItems: 10,	//总记录数
        itemsPerPage: 10,	//每页记录数
        perPageOptions: [10, 20, 30, 40, 50], //分页选项
        onChange: function () {     //当页码变更时自动触发的方法
            $scope.reloadList();   //重新加载
        }
    };
    $scope.reloadList = function () {
        // $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    $scope.selectIds=[];
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1);
        }
    }
    $scope.jsonToString = function (jsonString, key) {
        var json = JSON.parse(jsonString);
        var value = "";
        for (var i = 0; i < json.length; i++) {
            if (i>0){
                value += ",";
            }
            value += json[i][key];
        }
        return value;
    }



});