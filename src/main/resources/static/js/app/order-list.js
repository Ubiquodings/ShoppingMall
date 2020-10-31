var orderList = {
    init: function () {
        let _this = this;
        // 여기에 소켓이 있는 이유는 구매한 사용자수 가져오기 위해서 !!
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

                let productIdList = [];
                let userId = $('#input-user-id').val();
                let currentPage = 0;
                Array.from(productList).forEach((row) => {
                    // let product = JSON.parse(JSON.stringify(row.productId));
                    console.log('product: ' + JSON.stringify(row.productId));

                    let productId = row.id;
                    productIdList.push(productId);

                    // row.id 에 대해 소켓 구독이 필요하다!
                    if (stompClient.connected) {
                        /*[구독 1] 해당상품 구매한 사용자 수*/
                        /* send: /app/users/ordered/{productId} , subscribe: /topic/users/ordered/{productId}
                        * */
                        stompClient.subscribe('/topic/users/ordered/' + productId, function (result) { // 콜백 호출이 안되네! 왜지!??
                            let parseResult = JSON.parse(result.body);
                            console.log('/topic/users/{productId} 결과 :  \n' + parseResult.number); // ok
                            _this.updateUserNumber(parseResult.productId, parseResult.number);
                        }, {});

                        /*[요청 1] 해당상품 구매한 사용자 수*/
                        stompClient.send('/app/users/ordered/' + productId,
                            {"productId": productId}, {});

                    }

                    modalContentUI.innerHTML += _this.buildModalContents(
                        row.id, row.imgUrl, row.name, row.count, row.price
                    );
                });

                if(stompClient.connected){

                    /*[구독 2] 목록에서 함께산 상품목록*/
                    /* send: /app/products/buywith/order-list/{userId}/page/{currentPage} // 소켓 서버에서 userId 를 사용하진 않지만 , 서버>클라 데이터 전송하기 위해!
                    , subscribe: /topic/products/buywith/order-list/{userId}
                    * */
                    stompClient.subscribe('/topic/products/buywith/order-list/' + userId, function (result) { // 콜백 호출이 안되네! 왜지!??
                        let className = 'order-list-buy-together-list';
                        let resultList = JSON.parse(result.body); /*JSON.stringify(*/
                        // console.log('/topic/products/{userId} 결과 :  \n'+ resultList);

                        // 결과로 화면 조작
                        _this.updateRecommendedList(resultList, className);
                    }, {});


                    /*목록 주기적 요청*/
                    _this.setSchedulingTasks(stompClient, userId, currentPage, productIdList);

                }

                $("#exampleModalScrollable").modal('show');

            }).fail(function (e) {
                console.log('fail ' + JSON.stringify(e));
            });
        });

    },
    /*스케줄링 작업*/
    setSchedulingTasks: function(stompClient, userId, currentPage, productIdList){
        // 추천 목록 주기적 요청
        setInterval(function(){
            currentPage += 1;

            /*[요청 1] 해당 상품과 이런 상품도 함께 구매했어요*/
            stompClient.send('/app/products/buywith/order-list/' + userId+'/page/'+currentPage,
                {}, {'productIdList':productIdList});

        }, 2000);
    },




    /*order-list 추천목록 갱신*/
    updateRecommendedList: function (resultList, className) {
        let _this = this;
        let recomList = document.querySelector('div.' + className);
        recomList.innerHTML = "";

        Array.from(resultList).forEach((product) => {
            // console.log(product); // ok
            /*
{categoryId: 1
description: "국산 천일염만으로 절여진"
id: 99
imgUrl: "https://img-cf.kurly.com/shop/data/goods/1587000014157l0.jpg"
name: "오이지 3입"
price: 3990
stockQuantity: 50}
            * */
            let id = product.id;
            let imgUrl = product.imgUrl;
            let name = product.name;
            // let price = product.price;
            // let description = product.description;

            recomList.innerHTML += _this.buildHTML(id, imgUrl, name); // 출력 ok
        });
    },
    buildHTML: function (id, imgUrl, name) {

        return `<div class="col-md-3">
                    <!--이미지-->
                    <div class="row">
                        <img src="${imgUrl}" class="img-responsive"/>
                    </div>
                    <!--상품이름-->
                    <p>${name}</p>
                </div>`
    },


    /*사용자수 UI 갱신*/
    updateUserNumber: function (productId, number) {
        // list 가져와서
        Array.from(document.getElementsByClassName('productOrderUserNumber-' + productId))
            .forEach((userNumberDiv) => {
                // 부모의 첫번째 자식
                console.log('\nupdateOrderUserNumber : ' + userNumberDiv.parentElement.children[0].value);
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
                <div class="col-4">
                    <img class="img-responsive" src="${imgUrl}"/>
                </div>
                <div class="col-8">
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