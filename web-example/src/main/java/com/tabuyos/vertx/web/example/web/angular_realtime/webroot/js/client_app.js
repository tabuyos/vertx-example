/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

function CartController($scope, $filter, $http, $timeout) {

  $scope.items = [];
  $scope.orderSubmitted = false;
  $scope.username = '';
  $scope.password = '';
  $scope.loggedIn = false;
  $scope.eb = null;

  $scope.addToCart = function (album) {
    console.log("Adding to cart: " + JSON.stringify(album));
    for (var i = 0; i < $scope.items.length; i++) {
      var compare = $scope.items[i];
      if (compare.album._id === album._id) {
        compare.quantity = compare.quantity + 1;
        return;
      }
    }
    var item = {
      album: album,
      quantity: 1
    };
    $scope.items.push(item);
  };

  $scope.removeFromCart = function (item) {
    $scope.items = $scope.items.filter(function (v) {
      return v.album._id !== item.album._id;
    });
  };

  $scope.total = function () {
    var tot = 0;
    for (var i = 0; i < $scope.items.length; i++) {
      var item = $scope.items[i];
      tot += item.quantity * item.album.price;
    }
    return tot;
  };

  $scope.orderReady = function () {
    return $scope.items.length > 0 && $scope.loggedIn;
  };

  $scope.submitOrder = function () {
    if (!$scope.orderReady()) {
      return;
    }

    var orderItems = $filter('json')($scope.items);
    var orderMsg = {
      username: $scope.username,
      items: orderItems
    };

    $scope.eb.send('vtoons.placeOrder', orderMsg, function (err, reply) {
      if (err) {
        console.error('Failed to accept order');
        return;
      }
      $scope.orderSubmitted = true;
      // lets clear the cart now
      $scope.items = [];
      $scope.$apply();
      // Timeout the order confirmation box after 2 seconds
      // window.setTimeout(function() { $scope.orderSubmitted(false); }, 2000);
    });
  };

  $scope.login = function () {
    if ($scope.username.trim() != '' && $scope.password.trim() != '') {
      $http.post(window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/login', {
        username: $scope.username,
        password: $scope.password
      }).success(function (data, status) {
        $scope.loggedIn = true;

        $timeout(function () {
          if ($scope.eb != null) {
            $scope.eb.close();
          }

          $scope.eb = new EventBus(window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/eventbus');

          $scope.eb.onopen = function () {

            // Get the static data
            $scope.eb.send('vtoons.listAlbums', {}, function (err, reply) {
              if (err) {
                console.error('Failed to retrieve albums: ' + err);
                return;
              }
              $scope.albums = reply.body;
              $scope.$apply();
            });
          };

          $scope.eb.onclose = function () {
            $scope.eb = null;
          };

          $scope.$apply();
        });
      }).error(function () {
        alert('invalid login');
      });
    }
  };
}
