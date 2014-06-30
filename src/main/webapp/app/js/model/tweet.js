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

    var deps = ['lib/underscore', 'lib/backbone'];
    define(deps, function (underscore) {

        return Backbone.Model.extend({
            urlRoot: window.ux.ROOT_URL + 'rest/tweets',
            idAttribute: 'id',
            parse: function (response) {
                var result = {
                    id: response.id,
                    content: response.content,
                    createdAt: new Date(response.createdAt),
                    user: response.user.name
                };
                if (response.author) {
                    result.author = response.author.name;
                } else {
                    result.author = response.user.name;
                }
                if (response.mentions) {
                    if (underscore.isArray(response.mentions)) {
                        result.mentions = underscore.map(response.mentions, function (bean) {
                            return bean.name;
                        });
                    } else {
                        result.mentions = [response.mentions.name];
                    }
                }
                return result;
            }
        });
    });
}());


