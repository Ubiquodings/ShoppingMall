var productDetail = {
    init: function () {
        let _this = this;

        let productId = $('#input-product-id').val(); // 확인! ok
        console.log('현재 페이지 상품ID :  ' + $('#input-product-id').val() + typeof productId); // ok
        // typeof productId

        let userId = $('#input-user-id').val(); /*header 페이지에 있다*/
        // console.log('현재 페이지 유저ID :  ' + $('#input-user-id').val()); // ok

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
        let cur_index = 0;
        let item_list_size=4;

        stompClient.connect(/*header*/{}, function (frame) {


            /*[구독 1] TODO 해당 상품과 이런 상품도 함께 구매했어요*/
            /* send: /app/products/buywith/{productId}/page/{currentPage} // 소켓 서버에서 userId 를 사용하진 않지만 , 서버>클라 데이터 전송하기 위해!
            , subscribe: /topic/products/buywith/{productId}
            * */
            stompClient.subscribe('/topic/products/buywith/' + productId, function (result) { // 콜백 호출이 안되네! 왜지!??
                let className = 'product-detail-buy-together-list';
                let resultList = JSON.parse(result.body); /*JSON.stringify(*/
                // console.log('/topic/products/{userId} 결과 :  \n'+ resultList);

                // 결과로 화면 조작: 4개씩 다음으로 이동할 방법
                setInterval(function() {
                    console.log(cur_index);
                    _this.updateRecommendedList(resultList.slice(cur_index%resultList.length, (cur_index%resultList.length)+item_list_size), className);
                    cur_index += item_list_size;
                }, 2000);
            }, {});



            /*[구독 2] 망설이지마세요 쿠폰*/
            /* send: /app/coupons/{userId} , subscribe: /topic/coupons/{userId}
            * */
            stompClient.subscribe('/topic/coupons/' + userId, function (result) {
                result = JSON.parse(result.body); /*JSON.stringify(*/
                console.log('/topic/coupons/{userId} 결과 :  \n' + result.couponName); // coupone name ok

                // 결과로 화면 조작
                // _this.updateRecommendedList(resultList);
                // $("#btn-my-coupons").css("color","red");
                // alert("쿠폰이 도착했습니다!\n" + result.couponName);
                document.querySelector('#btn-my-coupons').innerHTML += ` <span class="badge badge-light">1</span>`;
                // console.log($("#btn-my-coupons").innerHTML);

                // alert('쿠폰이 발급되었습니다!');
            }, {});


            /*망설이지마세요 쿠폰 요청*/
            _this.requestDoNotHesitateCoupon(stompClient, userId, productId);

            /*스케줄링 작업 설정: 추천목록 갱신*/
            _this.setSchedulingTasks(stompClient, productId, currentPage);

        });


    },
    setSchedulingTasks: function(stompClient, productId, currentPage){
        // 추천 목록 요청
        /*[요청 1] 해당 상품과 이런 상품도 함께 구매했어요*/
        stompClient.send('/app/products/buywith/' + productId+'/page/'+currentPage,
            {}, {});

        // setInterval(function(){ //주기적
        //     currentPage += 1;
        // }, 2000);
    },
    requestDoNotHesitateCoupon: function (stompClient, userId, productId) {
        setTimeout(function () {
            console.log('망설이지마세요 쿠폰 요청');
            stompClient.send('/app/coupons/' + userId,
                {}, JSON.stringify({ // body 에 string 처리 꼭 해줘야하는구나!
                    productId: productId
                }));
        }, 3000);
    },
    updateRecommendedList: function(resultList, className){
        let _this = this;
        let recomList = document.querySelector('div.'+className);
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
            $.ajax({
                type: 'GET',
                url: '/api/hover/' + id,
                dataType: 'json',
                contentType: 'application/json; charset=utf-8'
            }).done(function () { // 왜 안되지 ?
                console.log('ok');
            }).fail(function (e) {
                console.log('fail ' + JSON.stringify(e));
            });

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

};

productDetail.init();