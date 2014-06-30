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

var APP_CONFIG = {
    baseUrl: window.ux.ROOT_URL,
    paths: {
        'text': 'webjars/requirejs-text/2.0.10/text',
        'lib/less': 'webjars/less/1.7.0/less.min',
        'lib/jquery': 'webjars/jquery/2.1.1/jquery.min',
        'lib/jquery-ui': 'webjars/jquery-ui/1.11.0/jquery-ui.min',
        'lib/handlebars': 'webjars/handlebars/1.3.0/handlebars.min',
        'underscore': 'webjars/underscorejs/1.6.0/underscore-min',
        'lib/json2': 'webjars/json2/20110223/json2.min',
        'backbone': 'webjars/backbonejs/1.1.2/backbone-min',
        'lib/bootstrap': 'webjars/bootstrap/3.2.0/js/bootstrap.min',
        'moment': 'webjars/momentjs/2.7.0/min/moment.min'
    },
    shim: {

        'lib/bootstrap': {
            deps: ['lib/jquery']
        },

        'lib/jquery-ui': {
            deps: ['lib/jquery']
        },

        'underscore': {
            exports: '_'
        },

        'backbone': {
            deps: ['lib/jquery', 'lib/json2', 'underscore']
        },

        'app/js/templates': {
            deps: ['underscore', 'app/js/i18n']
        },

        'app/js/models': {
            deps: ['underscore']
        }
    }
};