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
                // }
            });

        // let userNumberDiv = document.querySelector('div#userNumber');
        // userNumberDiv.innerHTML = "";
        // userNumberDiv.innerHTML += `<span class="material-icons orange600">visibility</span><span class="align-text-bottom"> ${number}명</span>`;
        // userNumberDiv.innerHTML += `<!--<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" width="16" height="16"><path fill-rule="evenodd" d="M1.679 7.932c.412-.621 1.242-1.75 2.366-2.717C5.175 4.242 6.527 3.5 8 3.5c1.473 0 2.824.742 3.955 1.715 1.124.967 1.954 2.096 2.366 2.717a.119.119 0 010 .136c-.412.621-1.242 1.75-2.366 2.717C10.825 11.758 9.473 12.5 8 12.5c-1.473 0-2.824-.742-3.955-1.715C2.92 9.818 2.09 8.69 1.679 8.068a.119.119 0 010-.136zM8 2c-1.981 0-3.67.992-4.933 2.078C1.797 5.169.88 6.423.43 7.1a1.619 1.619 0 000 1.798c.45.678 1.367 1.932 2.637 3.024C4.329 13.008 6.019 14 8 14c1.981 0 3.67-.992 4.933-2.078 1.27-1.091 2.187-2.345 2.637-3.023a1.619 1.619 0 000-1.798c-.45-.678-1.367-1.932-2.637-3.023C11.671 2.992 9.981 2 8 2zm0 8a2 2 0 100-4 2 2 0 000 4z"></path></svg>-->`;
    },

};
common.init();