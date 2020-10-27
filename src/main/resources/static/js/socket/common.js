var common = {
    init: function () {
        let _this = this;

        let userId = $('#input-user-id').val(); /*header 페이지에 있다*/
        console.log('현재 페이지 유저ID :  ' + userId); // ok

        /*소켓 연결*/
        $(document).ready(function () { // 문서가 준비되면
            console.log("문서 ready!");
            _this.connectSocket(userId);
        });
    },
    connectSocket: function (userId) {
        let _this = this;
        let socket = new SockJS('/websocket');
        let stompClient = Stomp.over(socket);

        // 소켓 연결이 끊어졌을 때, 필요한 자원 정리 처리 : 새로고침을 위해 ?
        window.onbeforeunload = function (eventObject) {
            if (stompClient.connected) {
                console.log('common.js');
                userId = $('#input-user-id').val();
                let productIdList = [];
                Array.from(document.getElementsByClassName('product-detail-id')) // list and detail
                    .forEach((productIdElem) => {
                        // productId 루프돌며 구독
                        let productId = productIdElem.value;
                        productIdList.push(productId);
                    });

                console.log("상품열람 사용자수 감소요청합니다: " + productIdList);

                stompClient.disconnect(function () { // 가설: 소켓 클라이언트는 사실 연결되어 있다! 모든 페이지 다른 클라이언트
                }, {"userId": userId, "productIdList": productIdList});
            }
        };

        stompClient.connect(/*header*/{"userId": userId}, function (frame) {

            /*[구독 1] 접속한 전체 사용자 수*/
            /* send: /app/users/root , subscribe: /topic/users/root
            * */
            stompClient.subscribe('/topic/users/root', function (result) { // 콜백 호출이 안되네! 왜지!??
                let parseResult = JSON.parse(result.body);
                console.log('/topic/users/root 결과 :  \n' + parseResult.number); // ok
                _this.updateUserNumber(parseResult.number);
            }, {});


            /*[요청 1] 접속한 전체 사용자 수*/
            stompClient.send('/app/users/root',
                {}, {});

        });
    },
    updateUserNumber: function (number) {
        // list 가져와서
        Array.from(document.getElementsByClassName('view-root'))
            .forEach((userNumberDiv) => {
                // 부모의 첫번째 자식
                // if(userNumberDiv.parentElement.children[0].value === productId){ // 해당 상품 열람 사용자 수 갱신
                console.log('\nupdateUserNumber : '+userNumberDiv.parentElement.children[0].value);
                userNumberDiv.innerHTML = "";
                userNumberDiv.innerHTML += `<span class="material-icons orange600 d-inline-block" tabindex="0" data-toggle="tooltip"
                      title="쇼핑몰 전체 접속자 수">accessibility_new</span><span class="align-text-bottom"> ${number}명</span>`;

            });

    },

};
common.init();