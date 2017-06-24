var UglifyJsPlugin = require("uglifyjs-webpack-plugin");
var webpack = require("webpack");
var webpackMerge = require("webpack-merge");
var commonConfig = require("./webpack.common.js");
module.exports = webpackMerge(commonConfig, {
    "devtool": "none",
    "plugins": [
        new UglifyJsPlugin({ mangle: false }),
        new webpack.DefinePlugin({
            PRODUCTION: JSON.stringify(true)
        })
    ]
});
