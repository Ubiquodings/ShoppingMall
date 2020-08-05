var productSocket = {
    init: function () {
        let _this = this;

        let productId = $('#input-product-id').val(); // 확인! ok
        console.log('현재 페이지 상품ID :  ' + $('#input-product-id').val() + typeof productId); // ok
        // typeof productId

        let userId = $('#input-user-id').val(); /*header 페이지에 있다*/
        console.log('현재 페이지 유저ID :  ' + $('#input-user-id').val()); // ok

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
        let currentPage = 0;

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
            stompClient.subscribe('/topic/products/'+userId, function (result) {

                console.log('/topic/products/{userId} 결과 :  \n'+ JSON.stringify( /*JSON.parse(*/result.body ));

                // 결과로 화면 조작

            }, {}); // "userId":userId -- 여기서는 header 필요없을듯!

            /*해당 페이지 접속 사용자 수 요청*/
            stompClient.send('/app/users/' + productId,
                {"productId": productId}, {});

            /*스케줄링 작업 설정*/
            _this.setSchedulingTasks(stompClient, userId, currentPage);
        });

        /*테스트 ok*/ // 소켓 연결이 끊어졌을 때, 필요한 자원 정리 처리
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
    },
    setSchedulingTasks: function(stompClient, userId, currentPage){
        setInterval(function(){
            console.log('userID 로그 확인 : '+userId);

            currentPage += 1
            // 디테일 추천목록 2초마다 받아서 화면에 뿌리기
            stompClient.send('/app/products/' + userId + '/page/'+currentPage,
                {}, {});
            console.log('추천 목록 요청');

            // 화면 구성하는 함수도 필요하겠다! 구독하는 쪽에 TODO
        }, 1000);
    }
};

productSocket.init();