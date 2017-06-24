import {Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild, ViewEncapsulation} from "@angular/core";
import {TwitterService} from "./service_twitter";
import {MainViewService} from "./service_mainview";
import * as moment from "moment";
import {LocalStorageService} from "angular-2-local-storage";

class TweetsColumn {
    handle: string;
    limit: number;
    tweets: Array<any>;
}

@Component({
    selector: "div[data-twittertrack-tweets]",
    templateUrl: "../templates/component_home.pug",
    styleUrls: ["../styles/component_home.sass"],
    encapsulation: ViewEncapsulation.None,
    providers: [TwitterService]
})
export class TweetsComponent implements OnInit {

    model: {
        columnA: TweetsColumn,
        columnB: TweetsColumn,
        columnC: TweetsColumn
    };

    constructor(private twitter: TwitterService, private storage: LocalStorageService) {
    }

    onReload() {
        console.log(`loading data...`);
        this.storage.set('columns', this.model);
        let columns = [this.model.columnA, this.model.columnB, this.model.columnC].filter((item) => item.handle && item.handle.trim() !== '');
        columns.forEach((column) => {
            this.twitter.getTweets(column.handle, column.limit).subscribe((data) => {
                column.tweets = data.map((entry) => {
                    entry.date = moment.utc(entry.date, 'YYYYMMDDHHmmss-zzzz').toDate();
                    return entry;
                });
            });
        });
    }

    ngOnInit(): void {
        this.model = this.storage.get('columns') as {
            columnA: TweetsColumn,
            columnB: TweetsColumn,
            columnC: TweetsColumn
        };
        if(!this.model) {
            this.model = {
                columnA: new TweetsColumn(),
                columnB: new TweetsColumn(),
                columnC: new TweetsColumn()
            };
        }
        this.onReload();
    }

}

@Component({
    selector: "div[data-twittertrack-tweet-column]",
    templateUrl: "../templates/component_tweet_column.pug",
    styleUrls: ["../styles/component_tweet_column.sass"],
    encapsulation: ViewEncapsulation.None
})
export class TweetColumnComponent {
    @Input() tweets;
}

@Component({
    selector: "div[data-twittertrack-config]",
    templateUrl: "../templates/component_config.pug"
})
export class ConfigComponent {

    @ViewChild("menuBodyTemplate")
    template: TemplateRef<any>;

    @Output() onReload = new EventEmitter<void>();

    @Input() model;

    constructor(private mainView: MainViewService) {
    }

    open(): void {
        this.mainView.get().createEmbeddedView(this.template);
    }

    save(): void {
        this.onReload.emit();
        this.mainView.get().clear();
    }

}
