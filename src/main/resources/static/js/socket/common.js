var common = {
    init: function () {
        let _this = this;

        /*소켓 연결*/
        $(document).ready(function () { // 문서가 준비되면
            console.log("문서 ready!");
            _this.connectSocket(productId, userId);
        });
    },
    connectSocket: function (productId, userId) {
        let _this = this;
        let socket = new SockJS('/websocket');
        let stompClient = Stomp.over(socket);

        // 소켓 연결이 끊어졌을 때, 필요한 자원 정리 처리
        window.onbeforeunload = function (eventObject) {

            stompClient.disconnect(function () {
            }, {"productId": productId});
        };

        stompClient.connect(/*header*/{"productId": productId}, function (frame) {

            /*[구독 1] 접속한 전체 사용자 수*/
            /* send: /app/users/root , subscribe: /topic/users/root
            * */
            stompClient.subscribe('/topic/users/' + productId, function (result) { // 콜백 호출이 안되네! 왜지!??
                console.log('/topic/users/{productId} 결과 :  \n' + JSON.parse(result.body).number); // ok
                _this.updateUserNumber(JSON.parse(result.body).number);
            }, {"productId": productId});


            /*[요청 1] 접속한 전체 사용자 수*/
            stompClient.send('/app/users/' + productId,
                {"productId": productId}, {});

        });
    }
};
common.init();