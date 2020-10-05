var orderNumber = {
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

            // productId : html 에 세팅하고 list 가져오기
            var productIdList = [];
            Array.from(document.getElementsByClassName('product-detail-id')) // list and detail
                .forEach((productIdElem) => {
                    // productId 루프돌며 구독
                    let productId = productIdElem.value;
                    productIdList.push(productId);

                    /*[구독 1] 해당상품 구매한 사용자 수*/
                    /* send: /app/users/ordered/{productId} , subscribe: /topic/users/ordered/{productId}
                    * */
                    stompClient.subscribe('/topic/users/ordered/' + productId, function (result) { // 콜백 호출이 안되네! 왜지!??
                        let parseResult = JSON.parse(result.body);
                        console.log('/topic/users/{productId} 결과 :  \n' + parseResult.number); // ok
                        _this.updateUserNumber(parseResult.productId, parseResult.number);
                    }, {"productId": productId});


                    /*[요청 1] 해당상품 구매한 사용자 수*/
                    stompClient.send('/app/users/ordered/' + productId,
                        {"productId": productId}, {});

                });
            console.log('productOrderUserNumber: ' + productIdList);

        });
    },
    updateUserNumber: function (productId, number) {
        // list 가져와서
        Array.from(document.getElementsByClassName('productOrderUserNumber-' + productId))
            .forEach((userNumberDiv) => {
                // 부모의 첫번째 자식
                console.log('\nupdateOrderUserNumber : '+userNumberDiv.parentElement.children[0].value);
                userNumberDiv.innerHTML = "";
                userNumberDiv.innerHTML += `<span class="material-icons light-green d-inline-block" tabindex="0" data-toggle="tooltip"
                                      title="이 상품을 구매한 사용자 수">payment</span><span class="align-text-bottom"> ${number}명</span>`;
                // }
            });
    },

};
orderNumber.init();