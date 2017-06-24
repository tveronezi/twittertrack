import {Injectable, ViewContainerRef} from "@angular/core";

@Injectable()
export class MainViewService {
    viewContainerRef: ViewContainerRef;

    set(viewContainerRef: ViewContainerRef) {
        this.viewContainerRef = viewContainerRef;
    }

    get() {
        return this.viewContainerRef;
    }
}