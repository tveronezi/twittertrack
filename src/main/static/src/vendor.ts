// Angular
import "@angular/platform-browser";
import "@angular/platform-browser-dynamic";
import "@angular/core";
import "@angular/common";
import "@angular/http";
import "@angular/router";
// RxJS
import "rxjs";

// Other vendors for example jQuery, Lodash or Bootstrap
// You can import js, ts, css, sass, ...

require("../node_modules/font-awesome/css/font-awesome.css");
require("../node_modules/lato-font/css/lato-font.css");
require("../node_modules/bootstrap/dist/css/bootstrap.css");

// Note that css-loader will resolve all url(...) in the css files as require(...)
// In the case of font-awesome, all images will match the configuration for images set up in "webpack.common.js"
