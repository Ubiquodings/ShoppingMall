var couponUse = {
    init: function () {
        let _this = this;

        /*소켓 연결*/
        $(document).ready(function () { // 문서가 준비되면
            console.log("문서 ready!");
            _this.connectSocket();
        });
    },
    connectSocket: function () {
        let _this = this;
        let socket = new SockJS('/websocket');
        let stompClient = Stomp.over(socket);

        // 소켓 연결이 끊어졌을 때, 필요한 자원 정리 처리
        window.onbeforeunload = function (eventObject) {

            // stompClient.disconnect(function () {
            // }, {"productId": productId});
        };

        stompClient.connect(/*header*/{}, function (frame) {

            // couponType : html 에 세팅하고 list 가져오기
            var couponTypeList = [];
            Array.from(document.getElementsByClassName('coupon-type')) // list and detail
                .forEach((couponTypeElem) => {
                    let couponType = couponTypeElem.value;
                    couponTypeList.push(couponType);

                    /*[구독 1] 해당 쿠폰 사용한 사용자 수*/
                    /* send: /app/users/coupons/{couponType} , subscribe: /topic/users/coupons/{couponType}
                    * */
                    stompClient.subscribe('/topic/users/coupons/' + couponType, function (result) { // 콜백 호출이 안되네! 왜지!??
                        let parseResult = JSON.parse(result.body);
                        console.log('/topic/users/{coupons} 결과 :  \n' + parseResult.number); // ok
                        _this.updateUserNumber(parseResult.couponType, parseResult.number);
                    }, {});


                    /*[요청 1] 해당 쿠폰 사용한 사용자 수*/
                    stompClient.send('/app/users/coupons/' + couponType,
                        {}, {});

                });
            console.log('productCouponUserNumber: ' + couponTypeList);


        });
    },
    updateUserNumber: function (couponType, number) {

        // list 가져와서
        Array.from(document.getElementsByClassName('couponUseUserNumber-' + couponType))
            .forEach((userNumberDiv) => {
                // 부모의 첫번째 자식
                console.log('\nupdateOrderUserNumber : '+userNumberDiv.parentElement.children[0].value);
                userNumberDiv.innerHTML = "";
                userNumberDiv.innerHTML += `<span class="material-icons pink00 d-inline-block" tabindex="0" data-toggle="tooltip"
                          title="이 쿠폰 사용한 사용자 수">check_circle_outline</span><span class="align-text-bottom"> ${number}명</span>`;
            });

    },

};
couponUse.init();