import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";

import {routing} from "./routing";

import {AppComponent} from "./component_app";
import {ConfigComponent, TweetsComponent, TweetColumnComponent} from "./component_tweets";
import {MainViewService} from "./service_mainview";

import { LocalStorageModule } from 'angular-2-local-storage';

@NgModule({
    imports: [
        LocalStorageModule.withConfig({
            prefix: 'twittertrack',
            storageType: 'localStorage'
        }),
        BrowserModule,
        FormsModule,
        HttpModule,
        routing
    ],
    declarations: [
        AppComponent,
        TweetsComponent,
        ConfigComponent,
        TweetColumnComponent
    ],
    providers: [MainViewService],
    bootstrap: [AppComponent]
})
export class AppModule {

}
