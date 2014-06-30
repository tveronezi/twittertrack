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

    var deps = ['app/js/templates', 'app/js/i18n', 'underscore'];
    define(deps, function (templates, i18n, underscore) {

        return Backbone.View.extend({
            el: 'body',

            render: function () {
                var me = this;
                var tweetsContainer;
                if (me.isRendered) {
                    tweetsContainer = me.$el.find('section > div.container-fluid > div.row').first();
                } else {
                    me.$el.html(templates.getValue('container', {}));
                    $(window.document).attr('title', i18n.get('application.name'));
                    me.isRendered = true;
                    tweetsContainer = me.$el.find('section > div.container-fluid > div.row').first();
                    tweetsContainer.sortable({
                        handle: "div.panel-heading",
                        update: function () {
                            if (!window.localStorage) {
                                return;
                            }
                            var users = [];
                            tweetsContainer.find('div.user-tweets-container').each(function (index, el) {
                                users.push($(el).data('twitter-account'));
                            });
                            window.localStorage.setItem('twittertrack-twitter-accounts', users.join(','));
                        }
                    });
                }
                me.$el.find('.twitter-list').remove();
                var tweetsMap = {};
                if (me.model) {
                    me.model.forEach(function (bean) {
                        var user = bean.get('user');
                        var myTweets = tweetsMap[user];
                        if (!myTweets) {
                            myTweets = [];
                            tweetsMap[user] = myTweets;
                        }
                        myTweets.push({
                            id: bean.get('id'),
                            user: bean.get('user'),
                            content: bean.get('content'),
                            author: bean.get('author')
                        });
                    });
                }
                var addUserTweets = function (myTweets, userName) {
                    var tweetEl = $(templates.getValue('tweets', {
                        tweets: myTweets,
                        user: userName
                    }));
                    tweetEl.data('twitter-account', userName);
                    tweetsContainer.append(tweetEl);
                };
                if (window.localStorage) {
                    var users = window.localStorage.getItem('twittertrack-twitter-accounts');
                    if (users && users.split(',')) {
                        underscore.each(users.split(','), function (userName) {
                            var myTweets = tweetsMap[userName];
                            if (myTweets) {
                                addUserTweets(myTweets, userName);
                            }
                            delete tweetsMap[userName];
                        });
                    }
                }
                underscore.each(tweetsMap, addUserTweets);
                return me;
            }
        });

    });
}());


