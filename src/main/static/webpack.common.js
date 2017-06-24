var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var LoaderOptionsPlugin = require("webpack/lib/LoaderOptionsPlugin");
var EncodingPlugin = require('webpack-encoding-plugin');
var path = require('path');
var fs = require('fs');
var docBase = process.env.DOC_BASE;
module.exports = {
    entry: {
        'polyfills': './src/polyfills.ts',
        'vendor': './src/vendor.ts',
        'app': './src/main.ts'
    },
    output: {
        path: docBase,
        filename: '[name].js',
        publicPath: 'app/',
        chunkFilename: '[id].chunk.js'
    },
    resolve: {
        extensions: ['.js', '.ts']
    },
    externals: {
        "marked": "marked"
    },
    module: {
        rules: JSON.parse(fs.readFileSync('webpack.rules.json')).map(function (item) {
            item.test = new RegExp(item.test, 'i');
            return item;
        })
    },
    plugins: [
        new LoaderOptionsPlugin({
            options: {
                tslint: {
                    emitErrors: false,
                    failOnHint: true,
                    fileOutput: {
                        dir: "target/ts-lint-reports/",
                        clean: true
                    },
                    rules: {
                        quotemark: [true, 'double']
                    }
                },
                htmlLoader: {
                    minimize: false // workaround for ng2
                }
            }
        }),
        new EncodingPlugin({
            encoding: 'utf-8'
        }),
        new webpack.optimize.CommonsChunkPlugin({
            name: ['app', 'vendor', 'polyfills']
        }),
        new webpack.ContextReplacementPlugin(
            /angular([\\\/])core([\\\/])@angular/,
            path.join(__dirname, './src')
        ),
        new HtmlWebpackPlugin({
            // disable default loader and simply load the raw jsp text
            template: '!!raw-loader!pug-html-loader!./src/index.pug',
            favicon: './src/favicon.ico',
            // replace the original index.html by this one manipulated file
            filename: 'index.html',
            chunks: ['polyfills', 'vendor', 'app'],
            hash: true
        })
    ]
};
