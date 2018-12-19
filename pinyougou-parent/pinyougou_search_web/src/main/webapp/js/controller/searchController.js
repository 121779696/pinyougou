app.controller("searchControler",function ($scope,searchService) {

    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
            }
        );

    }
});