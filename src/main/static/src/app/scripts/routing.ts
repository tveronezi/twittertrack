import {RouterModule} from "@angular/router";
import {TweetsComponent} from "./component_tweets";

export const routing = RouterModule.forRoot([
    {
        path: "",
        component: TweetsComponent
    }
]);
