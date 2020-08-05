var productSocket = {
    init: function () {
        let _this = this;

        let productId = $('#input-product-id').val(); // TODO 확인!
        console.log('현재 페이지 상품ID :  ' + $('#input-product-id').val() + typeof productId); // ok
        // typeof productId

        let userId = $('#input-user-id').val();
        console.log('현재 페이지 유저ID :  ' + $('#input-user-id').val()); // ok

        /*소켓 연결*/
        $(document).ready(function () {
            console.log("문서 ready!");
            _this.connectSocket(productId, userId);
        });
    },
    connectSocket: function (productId, userId) {
        let _this = this;
        let socket = new SockJS('/websocket');
        let stompClient = Stomp.over(socket);

        stompClient.connect(/*header*/{"productId": productId}, function (frame) {
            // setConnected(true); // 화면 조작 함수
            // console.log('Connected: ' + frame);

            /*[구독] 해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
            stompClient.subscribe('/topic/users/' + productId, function (result) { // 콜백 호출이 안되네! 왜지!??
                // showGreeting(JSON.parse(greeting.body).content); // 화면 조작 함수
                console.log('/topic/users/{productId} 결과 :  \n' + JSON.parse(result.body).number); // ok
                _this.updateUserNumber(JSON.parse(result.body).number);
            }, {"productId": productId});

            /*[구독] 해당 유저에게만 추천 목록 갱신*/
            // stompClient.subscribe('/topic/products/'+userId, function (result) {
            //     // showGreeting(JSON.parse(greeting.body).content); // 화면 조작 함수
            //     console.log('/topic/products/{userId} 결과 :  \n'+JSON.parse(result.body).content);
            // }, {"userId":userId});

            /*해당 페이지 접속 사용자 수 요청*/
            stompClient.send('/app/users/' + productId, {"productId": productId}, {body: ""});
        });

        /*TODO 테스트*/
        // window.onbeforeunload
        // stompClient.
        window.onbeforeunload = function (eventObject) { // 이거는 안먹힌다

            // 페이지를 그냥 이동하면 이게 실행이 안된다
            stompClient.disconnect(function () {
            }, {"productId": productId});
        };

    },
    updateUserNumber: function (number) {
        let userNumberDiv = document.querySelector('div#userNumber');
        userNumberDiv.innerHTML = "";
        userNumberDiv.innerHTML += `${number} 명`;
    }
};

productSocket.init();