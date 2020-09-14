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

            /*[구독] 해당 페이지 접속 사용자 수 브로드캐스트 갱신*/
            /* send: /app/users/{productId} , subscribe: /topic/users/{productId}
            * */
            stompClient.subscribe('/topic/users/' + productId, function (result) { // 콜백 호출이 안되네! 왜지!??
                console.log('/topic/users/{productId} 결과 :  \n' + JSON.parse(result.body).number); // ok
                _this.updateUserNumber(JSON.parse(result.body).number);
            }, {"productId": productId});

            /*[구독] 해당 유저에게만 추천 목록 갱신: 카테고리 기반*/
            /* send: /app/products/{userId}/page/{currentPage} , subscribe: /topic/products/{userId}
            * */
            stompClient.subscribe('/topic/products/'+userId, function (result) {
                let resultList = JSON.parse(result.body ); /*JSON.stringify(*/
                // console.log('/topic/products/{userId} 결과 :  \n'+ resultList);

                // 결과로 화면 조작
                _this.updateRecommendedList(resultList);
            }, {});

            /*[구독] 망설이지마세요 쿠폰*/
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


            /*해당 페이지 접속 사용자 수 요청*/
            stompClient.send('/app/users/' + productId,
                {"productId": productId}, {});

            /*스케줄링 작업 설정*/
            _this.setSchedulingTasks(stompClient, userId, currentPage);

            /*망설이지마세요 쿠폰 요청*/
            _this.requestDoNotHesitateCoupon(stompClient, userId, productId);
        });

        // 소켓 연결이 끊어졌을 때, 필요한 자원 정리 처리
        window.onbeforeunload = function (eventObject) {

            stompClient.disconnect(function () {
            }, {"productId": productId});
        };


    },
    updateUserNumber: function (number) {
        let userNumberDiv = document.querySelector('div#userNumber');
        userNumberDiv.innerHTML = "";
        userNumberDiv.innerHTML += `<span class="material-icons orange600">visibility</span><span class="align-text-bottom"> ${number}명</span>`;
        // userNumberDiv.innerHTML += `<!--<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" width="16" height="16"><path fill-rule="evenodd" d="M1.679 7.932c.412-.621 1.242-1.75 2.366-2.717C5.175 4.242 6.527 3.5 8 3.5c1.473 0 2.824.742 3.955 1.715 1.124.967 1.954 2.096 2.366 2.717a.119.119 0 010 .136c-.412.621-1.242 1.75-2.366 2.717C10.825 11.758 9.473 12.5 8 12.5c-1.473 0-2.824-.742-3.955-1.715C2.92 9.818 2.09 8.69 1.679 8.068a.119.119 0 010-.136zM8 2c-1.981 0-3.67.992-4.933 2.078C1.797 5.169.88 6.423.43 7.1a1.619 1.619 0 000 1.798c.45.678 1.367 1.932 2.637 3.024C4.329 13.008 6.019 14 8 14c1.981 0 3.67-.992 4.933-2.078 1.27-1.091 2.187-2.345 2.637-3.023a1.619 1.619 0 000-1.798c-.45-.678-1.367-1.932-2.637-3.023C11.671 2.992 9.981 2 8 2zm0 8a2 2 0 100-4 2 2 0 000 4z"></path></svg>-->`;
    },
    setSchedulingTasks: function(stompClient, userId, currentPage){
        // 추천 목록 주기적 요청
        setInterval(function(){
            currentPage += 1
            // 디테일 추천목록 2초마다 받아서 화면에 뿌리기
            stompClient.send('/app/products/' + userId + '/page/'+currentPage,
                {}, {});
        }, 2000);
    },
    updateRecommendedList: function(resultList){
        let _this = this;
        let recomList = document.querySelector('div.recommended-product-list');
        recomList.innerHTML = "";

        Array.from(resultList).forEach((product)=>{
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
            let price = product.price;
            let description = product.description;

            recomList.innerHTML += _this.buildHTML(id, imgUrl, name, price, description); // 출력 ok
        });
    },
    buildHTML: function(id, imgUrl, name, price, description){

        return `<div class="col-md-3 product-list-card-body">
            <input type="hidden" value="${id}"/>
            <div class="ibox">
                <div class="ibox-content product-box">
                    <div class="product-imitation">
                        <img src="${imgUrl}"/>
                    </div>
                    <div class="product-desc">
                    <!--
                    <span class="product-discount">
                        save 10%
                    </span>
                    -->
                        <a href="#" class="product-name">${name}</a>
                        <small class="product-price">${price}</small>
                        <div class="small m-t-xs description">
                            ${description}
                        </div>
                        <div class="m-t text-left">
                            <a href="#" class="btn btn-xs btn-outline btn-success" style="margin-top: 10px">장바구니 </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>`
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

productSocket.init();