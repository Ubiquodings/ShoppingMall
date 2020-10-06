var orderList = {
    init: function () {
        let _this = this;
        let socket = new SockJS('/websocket');
        let stompClient = Stomp.over(socket);
        stompClient.connect(/*header*/{}, function (frame) {
        });


        /* 주문 목록 클릭하면 */
        $(document).on('click', '.order-list-body', function (e) {
            // $(".btn-order-cancel").on('click', function (e) { // detail 페이지에서 가져와야지
            var orderId = this.children[0].value;
            console.log(orderId);

            $.ajax({
                type: 'GET',
                url: '/api/order-products/' + orderId,
                dataType: 'json',
                contentType: 'application/json; charset=utf-8'
            }).done(function (result) {

                // 결과 파싱
                let resultString = JSON.stringify(result);
                let productList = JSON.parse(resultString);

                // 화면 준비
                let modalContentUI = document.querySelector('#order-product-list');
                modalContentUI.innerHTML = "";

                Array.from(productList).forEach((row) => {
                    // let product = JSON.parse(JSON.stringify(row.productId));
                    console.log('product: ' + JSON.stringify(row.productId));

                    let productId = row.id;
                    // row.id 에 대해 소켓 구독이 필요하다!
                    if (stompClient.connected) {
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
                    }

                    modalContentUI.innerHTML += _this.buildModalContents(
                        row.id, row.imgUrl, row.name, row.count, row.price
                    );
                });

                $("#exampleModalScrollable").modal('show');

            }).fail(function (e) {
                console.log('fail ' + JSON.stringify(e));
            });
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
    buildModalContents: function (productId, imgUrl, productName, count, price) {
        let _this = this;
        return `
            <div class="row">
                <div class="col-3">
                    <img class="img-responsive" src="${imgUrl}"/>
                </div>
                <div class="col-9">
                    <p>${productName}</p>
                    <p>${count} 개 ${price} 원</p>
                    
                    <input type="hidden" value="${productId}" class="product-detail-id"/>
                    <!--이 상품을 구매한 사람 수--> <!--돈 다발-->
                    <div class="col mini-text-font productOrderUserNumber-${productId}" id="userOrderNumber" style="margin-top: 10px; margin-left: 0">
                    <span class="material-icons pink00 d-inline-block" tabindex="0" data-toggle="tooltip"
                          title="이 상품을 구매한 사람 수">payment</span>
                        <span class="align-text-bottom">0명</span>
                    </div>
                </div>
            </div>`;
    }
};

orderList.init();