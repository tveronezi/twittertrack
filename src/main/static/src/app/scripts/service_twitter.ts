import {Injectable} from "@angular/core";
import {Http} from "@angular/http";

@Injectable()
export class TwitterService {

    constructor(private http: Http) {}

    getTweets(user: string, counter: number) {
        return this.http.get(`/api/twitter/${user}/${counter}`).map((res) => res.json());
    }
}