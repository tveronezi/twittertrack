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

    var deps = ['app/js/templates', 'app/js/i18n'];
    define(deps, function (templates, i18n) {

        return Backbone.View.extend({
            el: 'body',

            initialize: function (config) {
                var me = this;
                if (!me.options) {
                    me.options = {};
                }
            },

            render: function () {
                var me = this;
                if (!me.options.isRendered) {
                    me.$el.html(templates.getValue('container', {}));
                    $(window.document).attr('title', i18n.get('application.name'));
                    // render it only once
                    me.options.isRendered = true;
                }

                return me;
            }
        });

    });
}());


