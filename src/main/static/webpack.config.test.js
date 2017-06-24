var fs = require("fs");
var rules = JSON.parse(fs.readFileSync("webpack.rules.test.json")).map(function (item) {
    item.test = new RegExp(item.test, "i");
    return item;
});
module.exports = {
    "devtool": "inline-source-map",
    "resolve": {
        "extensions": [".ts", ".js"]
    },
    "module": {
        "rules": rules
    }
};
