//控制层
app.controller('goodsController', function ($scope, $controller, goodsService,$location,uploadService,itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        //获取参数值
        var id = $location.search()['id'];

        if (id == null){
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                editor.html($scope.entity.goodsDesc.introduction);

                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
            /*    for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);

                }*/
                for( var i=0;i<$scope.entity.itemList.length;i++ ){
                    $scope.entity.itemList[i].spec =
                        JSON.parse( $scope.entity.itemList[i].spec);
                }
            }
        );
    }

    //保存
    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert("保存成功")
                    $scope.entity = {};
                    edit.html("");//清空富文本编辑器
                } else {
                    alert(response.message);
                }
            }
        );
    }
    //保存
    $scope.save = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        var object;
        if ($scope.entity.goods.id!=null){
            object = goodsService.update($scope.entity);
        }else {
            object = goodsService.add($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert("保存成功")
                  /*  $scope.entity = {};
                    edit.html("");//清空富文本编辑器*/
                    location.href="goods.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.message;
                }
                else {
                    alert(response.message);
                }
            }
        );
    }

    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}}
    //$scope.entity={goods:{},goodsDesc:{itemImages:[]}};//定义页面实体结构
    $scope.add_image=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    $scope.remove_image=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    $scope.selectItemCatList1=function () {
        itemCatService.findByParentId(0).success(
          function (response) {
              $scope.itemCatList1=response;
          }
        );
    }
    $scope.$watch("entity.goods.category1Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCatList2=response;
            }
        );
    });
    $scope.$watch("entity.goods.category2Id",function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCatList3=response;
            }
        );
    });
    $scope.$watch("entity.goods.category3Id",function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        );
    });

    $scope.$watch("entity.goods.typeTemplateId",function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;
                $scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);

                if ($location.search()['id'] == null){
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);

                }
            }
        );
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList=response;
            }
        );
    });

    $scope.updatSpecAttribute = function ($event,name, value) {
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
        if (object !=null){
            if ($event.target.checked){
                object.attributeValue.push(value);
            }else {
                object.attributeValue.splice(object.attributeValue.indexOf(value),1);
                if (object.attributeValue.length ==0){
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object),1);
                }
            }
        }else {
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
        }
    }



    $scope.createItemList= function () {
        $scope.entity.itemList=[{spec:{},price:0,num:9999,status:"0",isDefault:"0"}];

        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList=addColum($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
        }
    }

    addColum=function (list, key, keyValues) {
        var newList=[];
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < keyValues.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));
                newRow.spec[key]=keyValues[j];
                newList.push(newRow);
            }
        }
        return newList;

    }

    $scope.status=["未审核","已审核","审核未通过","关闭"];

    $scope.itemCatList=[];
    $scope.findItemCatList=function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id]=response[i].name;

                }
            }
        );
    }
    $scope.checkAttributeValue=function (key, keyValue) {
        var items = $scope.entity.goodsDesc.specificationItems;

        var object = $scope.searchObjectByKey(items,"attributeName",key);
        if (object != null){
            if (object.attributeValue.indexOf(keyValue) >= 0){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }

    }
});	
