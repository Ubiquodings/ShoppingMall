var productDetail = {
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

            /*[구독 3] 망설이지마세요 쿠폰*/
            /* send: /app/coupons/{userId} , subscribe: /topic/coupons/{userId}
            * */
            stompClient.subscribe('/topic/coupons/'+userId, function (result) {
                result = JSON.parse(result.body ); /*JSON.stringify(*/
                console.log('/topic/coupons/{userId} 결과 :  \n'+ result.couponName); // coupone name ok

                // 결과로 화면 조작
                // _this.updateRecommendedList(resultList);
                // $("#btn-my-coupons").css("color","red");
                alert("쿠폰이 도착했습니다!\n"+result.couponName);
                document.querySelector('#btn-my-coupons').innerHTML += ` <span class="badge badge-light">1</span>`;
                // console.log($("#btn-my-coupons").innerHTML);

                // alert('쿠폰이 발급되었습니다!');
            }, {});


            /*망설이지마세요 쿠폰 요청*/
            _this.requestDoNotHesitateCoupon(stompClient, userId, productId);
        });

        // 소켓 연결이 끊어졌을 때, 필요한 자원 정리 처리
        window.onbeforeunload = function (eventObject) {

            stompClient.disconnect(function () {
            }, {"productId": productId});
        };


    },
    requestDoNotHesitateCoupon: function(stompClient, userId, productId){
        setTimeout(function(){
            console.log('망설이지마세요 쿠폰 요청');
            stompClient.send('/app/coupons/' + userId,
                {}, JSON.stringify({ // body 에 string 처리 꼭 해줘야하는구나!
                    productId: productId
                }));
        }, 3000);
    }
};

productDetail.init();