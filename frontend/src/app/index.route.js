(function () {
    'use strict';

    angular
        .module('kudos')
        .config(routerConfig);

    /** @ngInject */
    function routerConfig($stateProvider, $urlRouterProvider, $locationProvider) {

        $stateProvider
            .state('showId', {
                url: '/kudos/for/:id',
                templateUrl: 'app/kudos/kudos.html',
                controller: 'KudosController',
                controllerAs: 'vm'
            })
            .state('random', {
                url: '/kudos/random',
                templateUrl: 'app/kudos/kudos.html',
                controller: 'KudosController',
                controllerAs: 'vm'
            });

        $urlRouterProvider.otherwise('/kudos/random');

        $locationProvider.html5Mode({
            enabled: true,
            requireBase: false
        });
    }

})();
