var dashBoard = {
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
        // window.onbeforeunload = function (eventObject) {
        //
        //     stompClient.disconnect(function () {
        //     }, {"productId": productId});
        // };

        stompClient.connect(/*header*/{"productId": productId}, function (frame) {

            /*[구독 1] 내가 본 상품의 연관 상품*/
            /* send: /app/products/related/{userId}/page/{currentPage}
            , subscribe: /topic/products/related/{userId} */
            stompClient.subscribe('/topic/products/related/' + userId, function (result) { // 콜백 호출이 안되네! 왜지!??
                console.log('/topic/users/{productId} 결과 :  \n' + JSON.parse(result.body).number); // ok
                _this.updateUserNumber(JSON.parse(result.body).number);
            }, {"productId": productId});

            /*[구독 2] xx님을 위한 추천상품*/
            /* send: /app/products/usercf/{userId}/page/{currentPage}
            , subscribe: /topic/products/usercf/{userId} */
            stompClient.subscribe('/topic/users/' + productId, function (result) { // 콜백 호출이 안되네! 왜지!??
                console.log('/topic/users/{productId} 결과 :  \n' + JSON.parse(result.body).number); // ok
                _this.updateUserNumber(JSON.parse(result.body).number);
            }, {"productId": productId});

            /*[구독 3] 요즘 잘나가는 상품*/
            /* send: /app/products/freq/page/{currentPage}
            , subscribe: /topic/products/freq */
            stompClient.subscribe('/topic/users/' + productId, function (result) { // 콜백 호출이 안되네! 왜지!??
                console.log('/topic/users/{productId} 결과 :  \n' + JSON.parse(result.body).number); // ok
                _this.updateUserNumber(JSON.parse(result.body).number);
            }, {"productId": productId});


            /*[요청 1] 내가 본 상품의 연관 상품*/
            stompClient.send('/app/users/' + productId,
                {"productId": productId}, {});

            /*[요청 2] xx님을 위한 추천상품*/
            stompClient.send('/app/users/' + productId,
                {"productId": productId}, {});

            /*[요청 3] 요즘 잘나가는 상품*/
            stompClient.send('/app/users/' + productId,
                {"productId": productId}, {});

        });
    }
};
dashBoard.init();