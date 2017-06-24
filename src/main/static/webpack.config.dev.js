var UglifyJsPlugin = require("uglifyjs-webpack-plugin");
var webpack = require("webpack");
var webpackMerge = require("webpack-merge");
var commonConfig = require("./webpack.common.js");
var docBase = process.env.DOC_BASE_DEV;
module.exports = webpackMerge(commonConfig, {
    output: {
        path: docBase,
        filename: '[name].js',
        publicPath: 'app/',
        chunkFilename: '[id].chunk.js'
    },
    "plugins": [
        new webpack.DefinePlugin({
            PRODUCTION: JSON.stringify(false)
        }),
        new webpack.SourceMapDevToolPlugin({
            filename: '[name].js.map',
            exclude: ['vendor.js', 'polyfills.js']
        }),
        new UglifyJsPlugin({
            mangle: false,
            exclude: ['app.js']
        })
    ]
});
