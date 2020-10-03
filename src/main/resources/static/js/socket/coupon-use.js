var couponUse = {
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

            // TODO couponType : html 에 세팅하고 list 가져오기
            /*[구독 1] 해당 쿠폰 사용한 사용자 수*/
            /* send: /app/users/coupons/{couponType} , subscribe: /topic/users/coupons/{couponType}
            * */
            stompClient.subscribe('/topic/users/coupons/' + couponType, function (result) { // 콜백 호출이 안되네! 왜지!??
                console.log('/topic/users/{productId} 결과 :  \n' + JSON.parse(result.body).number); // ok
                _this.updateUserNumber(JSON.parse(result.body).number);
            }, {"productId": productId});


            /*[요청 1] 해당 쿠폰 사용한 사용자 수*/
            stompClient.send('/app/users/coupons/' + couponType,
                {"productId": productId}, {});

        });
    },
    updateUserNumber: function (number) {
        let userNumberDiv = document.querySelector('div#userNumber');
        userNumberDiv.innerHTML = "";
        userNumberDiv.innerHTML += `<span class="material-icons orange600">visibility</span><span class="align-text-bottom"> ${number}명</span>`;
        // userNumberDiv.innerHTML += `<!--<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" width="16" height="16"><path fill-rule="evenodd" d="M1.679 7.932c.412-.621 1.242-1.75 2.366-2.717C5.175 4.242 6.527 3.5 8 3.5c1.473 0 2.824.742 3.955 1.715 1.124.967 1.954 2.096 2.366 2.717a.119.119 0 010 .136c-.412.621-1.242 1.75-2.366 2.717C10.825 11.758 9.473 12.5 8 12.5c-1.473 0-2.824-.742-3.955-1.715C2.92 9.818 2.09 8.69 1.679 8.068a.119.119 0 010-.136zM8 2c-1.981 0-3.67.992-4.933 2.078C1.797 5.169.88 6.423.43 7.1a1.619 1.619 0 000 1.798c.45.678 1.367 1.932 2.637 3.024C4.329 13.008 6.019 14 8 14c1.981 0 3.67-.992 4.933-2.078 1.27-1.091 2.187-2.345 2.637-3.023a1.619 1.619 0 000-1.798c-.45-.678-1.367-1.932-2.637-3.023C11.671 2.992 9.981 2 8 2zm0 8a2 2 0 100-4 2 2 0 000 4z"></path></svg>-->`;
    },

};
couponUse.init();