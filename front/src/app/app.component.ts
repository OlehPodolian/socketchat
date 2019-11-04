import {Component, OnInit} from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {Subject} from "rxjs";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    title = 'app';

    subject = new Subject();
    stompClient: any;
    name: string;
    disabled: boolean;
    sessionId: any;

    ngOnInit(): void {
        this.connect();
        this.subject.subscribe((id) => {
            this.sessionId = id;
            this.stompClient.subscribe('/user/queue/orders', function (notification) {
                // todo please do smth useful
            });
        });
    }

    register(sessionId: any, email: string) {
        var message = {"sessionId": sessionId, email: email};
        var body = JSON.stringify(message);
        this.stompClient.send("/app/register", {}, body);
    }

    unregister(sessionId: any, email: string) {
        var message = {"sessionId": sessionId, email: email};
        var body = JSON.stringify(message);
        this.stompClient.send("/app/unregister", {}, body);
    }

    connect() {
        const socket = new SockJS('http://localhost:8787/app');
        this.stompClient = Stomp.over(socket);
        setTimeout(() => {
            const urlarray = socket._transport.url.split('/');
            const index = urlarray.length - 2;
            this.sessionId = urlarray[index];
            this.subject.next(this.sessionId)
        }, 1000)
        this.stompClient.connect();
        this.setConnected(true);
    }

    disconnect() {
        this.unregister(this.sessionId, this.name);
        if (this.stompClient != null) {
            this.stompClient.ws.close();
        }
        this.setConnected(false);
        console.log("Disconnected");
    }

    sendName() {
        this.register(this.sessionId, this.name);
        this.stompClient.subscribe('/user/queue/notify/' + this.name, function (notification) {
            console.log(notification.body);
        });
    }

    setConnected(connected) {
        this.disabled = connected;
    }
}
