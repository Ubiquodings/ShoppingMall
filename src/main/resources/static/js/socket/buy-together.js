// var buyTogether = {
//     init: function () {
//         let _this = this;
//
//         /*소켓 연결*/
//         $(document).ready(function () { // 문서가 준비되면
//             console.log("문서 ready!");
//             _this.connectSocket();
//         });
//     },
//     connectSocket: function () {
//         let _this = this;
//         let socket = new SockJS('/websocket');
//         let stompClient = Stomp.over(socket);
//
//
//         stompClient.connect(/*header*/{}, function (frame) {
//
//             // class name : product-detail-id 으로 product id list 가져올 수 있다
//             // 근데 detail 은 1개이고, order-list 는 여러개잖아! body 로 전달해야하나 ?
//             // 아니야. order 의 buy-together 를 여기에 할수가 없다!
//             // app/order-list.js 에 ajax 와 연동해야 한다!
//
//             // /*[구독 1] 해당 상품과 이런 상품도 함께 구매했어요*/
//             // /* send: /app/products/buywith/{productId}/page/{currentPage} // 소켓 서버에서 userId 를 사용하진 않지만 , 서버>클라 데이터 전송하기 위해!
//             // , subscribe: /topic/products/buywith/{productId}
//             // * */
//             // stompClient.subscribe('/topic/users/' + productId, function (result) { // 콜백 호출이 안되네! 왜지!??
//             //     console.log('/topic/users/{productId} 결과 :  \n' + JSON.parse(result.body).number); // ok
//             //     _this.updateUserNumber(JSON.parse(result.body).number);
//             // }, {});
//             //
//             //
//             // /*[요청 1] 해당 상품과 이런 상품도 함께 구매했어요*/
//             // stompClient.send('/app/users/' + productId,
//             //     {}, {});
//
//         });
//     }
// };
// buyTogether.init();