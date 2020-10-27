var dashAndProductDetail = {
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
        let currentPage = 0;
        let userId = $('#input-user-id').val();


        stompClient.connect(/*header*/{}, function (frame) {

            // TODO 개인화
            /*[구독 1] xx님을 위한 할인 상품*/
            /* send: /app/products/discount/{userId}/page/{currentPage}
            , subscribe: /topic/products/discount/{userId}
            * */
            stompClient.subscribe('/topic/products/discount/' + userId, function (result) { // 콜백 호출이 안되네! 왜지!??
                let className = 'product-detail-recommended-list';
                let resultList = JSON.parse(result.body ); /*JSON.stringify(*/
                // console.log('/topic/products/{userId} 결과 :  \n'+ resultList);

                // 결과로 화면 조작
                _this.updateRecommendedList(resultList, className);
            }, {});

            // TODO 개인화
            /*[구독 2] 상품 카테고리 기반 목록*/
            /* send: /app/products/{userId}/page/{currentPage}
            , subscribe: /topic/products/{userId}
            * */
            stompClient.subscribe('/topic/products/'+userId, function (result) {
                let className = 'recommended-product-list';
                let resultList = JSON.parse(result.body ); /*JSON.stringify(*/
                // console.log('/topic/products/{userId} 결과 :  \n'+ resultList);

                // 결과로 화면 조작
                _this.updateRecommendedList(resultList, className);
            }, {});


            /*[요청 1] xx님을 위한 할인 상품*/
            /*[요청 2] 상품 카테고리 기반 목록*/
            /*스케줄링 작업 설정: 추천목록 갱신*/
            _this.setSchedulingTasks(stompClient, userId, currentPage);

        });
    },
    setSchedulingTasks: function(stompClient, userId, currentPage){
        // 추천 목록 주기적 요청
        setInterval(function(){
            currentPage += 1;

            /*[요청 1] xx님을 위한 할인 상품*/
            stompClient.send('/app/products/discount/'+userId+'/page/' + currentPage,
                {}, {});

            /*[요청 2] 상품 카테고리 기반 목록*/
            // 디테일 추천목록 2초마다 받아서 화면에 뿌리기
            stompClient.send('/app/products/' + userId + '/page/'+currentPage,
                {}, {});
        }, 2000);
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
dashAndProductDetail.init();