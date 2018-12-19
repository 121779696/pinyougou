app.controller("indexController",function ($scope,$controller,loginServie) {


        $scope.showLoginName =function () {
            loginServie.loginName().success(
                function (response) {
                    $scope.loginName = response.longinName;
                }
            );
        }
});