import {AfterViewInit, Component, ViewContainerRef, ViewEncapsulation} from "@angular/core";
import {MainViewService} from "./service_mainview";

@Component({
    selector: "div[data-twittertrack]",
    templateUrl: "../templates/component_app.pug",
    styleUrls: ["../styles/main.sass", "../styles/component_app.sass"],
    encapsulation: ViewEncapsulation.None
})
export class AppComponent implements AfterViewInit {

    ngAfterViewInit(): void {
        this.mainView.set(this.viewContainerRef);
    }

    constructor(private viewContainerRef: ViewContainerRef,
                private mainView: MainViewService) {
    }

}
