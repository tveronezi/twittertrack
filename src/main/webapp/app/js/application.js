/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function () {
    'use strict';

    var deps = [
        'app/js/view/container',
        'app/js/model/tweets',
        'lib/underscore',
        'app/js/i18n',
        'app/js/keep-alive',
        'lib/less', 'lib/backbone', 'lib/jquery'
    ];
    define(deps, function (ContainerView, tweets, underscore, i18n, ping) {
        $.ajaxSetup({ cache: false });

        function start() {
            var containerView = new ContainerView({
                options: {
                    model: tweets
                }
            });

            //Starting the backbone router.
            var Router = Backbone.Router.extend({
                routes: {
                    '': 'home'
                }
            });
            var router = new Router();
            router.on("route:home", function () {
                containerView.render();
            });

            //Starting the backbone history.
            Backbone.history.start({
                pushState: true,
                root: window.ux.ROOT_URL // This value is set by <c:url>
            });

            ping.start();
            tweets.fetch({
                success: function () {
                    containerView.render();
                    containerView.listenTo(tweets, 'reset add change remove', containerView.render);
                }
            });

            return {
                getRouter: function () {
                    return router;
                }
            };
        }

        return {
            start: start
        };
    });
}());