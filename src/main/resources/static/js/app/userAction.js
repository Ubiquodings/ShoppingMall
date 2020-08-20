var userAction = {
    init: function () {
        var _this = this;

        $("#btn-order-cancel").on('click', function (e) { // detail 페이지에서 가져와야지
            // var productId = this.children[0].value;
            $.ajax({
                type: 'DELETE',
                url: '/api/orders/' + $("input#input-order-id").val(), // order id
                dataType: 'json',// Accept ?
                contentType: 'application/json; charset=utf-8',
                // data: JSON.stringify({})
            }).done(function () {
                alert('삭제되었습니다.');
                // window.location.href=window.location.href;
            }).fail(function (error) {
                alert(JSON.stringify(error));
            });
            window.location.href = window.location.href;
        });


        $(".btn-shoplist").on('click', function (e) { // detail 페이지에서 가져와야지
            var productId = this.children[0].value;
            //var count = this.children[1].value;
            var count = $('#count').val();
            $.ajax({
                type: 'POST',
                url: '/api/carts/new/' + productId,
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                //data: JSON.stringify({}) // TODO 간단하게 url param 으로 대체
                data: JSON.stringify(count)
            }).done(function () { // 왜 안되지 ?
                alert('ok');
                //location.reload(); //새로고침 추가
                //$("order-list-row").load(window.location.href + "order-list-row");
                window.location.href=window.location.href; // reloading ?

            }).fail(function (e) {
                alert('fail ' + JSON.stringify(e));
            });
            //$("order-list-row").load(window.location.href + "order-list-row");
             window.location.href=window.location.href; // reloading ?
        });
        // button 에 input 넣기
        // button class 수정
        $(".btn-order-immediately-from-detail").on('click', function (e) { // detail 페이지에서 가져와야지
            var productId = this.children[0].value;
            var count = $('#count').val();
            $.ajax({
                type: 'POST',
                url: '/api/orders/fromDetail/' + productId,//$("input#input-cart-order").val(),
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(count)
            }).done(function () { // 왜 안되지 ?
                alert('ok');
            }).fail(function (e) {
                alert('fail ' + JSON.stringify(e));
            });
        });
        $(".btn-order-immediately-from-cart").on('click', function (e) { // detail 페이지에서 가져와야지
            var shopListId = this.children[0].value;
            var count = this.children[1].value;
            $.ajax({
                type: 'POST',
                url: '/api/orders/fromShopList/' + shopListId,//$("input#input-cart-order").val(),
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(count)
            }).done(function () {
                alert('ok');
                window.location.href = window.location.href;
            }).fail(function (e) {
                alert('fail ' + JSON.stringify(e));
            });

        });

        // 결재 페이지: 주문
        $(".btn-order-all").on('click', function (e) {
            let shopListIdList = []; // = $("#input-shop-list-id").val(); // get list ? no
            let couponIdList = $("#input-coupon-id").val();
            let shopListIdElem = document.getElementsByClassName('input-shop-list-id');
            Array.from(shopListIdElem).forEach((elem) => {
                console.log('장바구니id ' + elem.value);
                shopListIdList.push(elem.value);
            });
            console.log('shopListIdList: ' + shopListIdList + "\ncouponIdList: " + couponIdList);

            // $.ajax({
            //     type: 'POST',
            //     url: '/api/orderAll',
            //     dataType: 'json',
            //     contentType:'application/json; charset=utf-8',
            //     data: JSON.stringify({
            //         couponIdList:[],
            //         shopListIdList:[]
            //     })
            // }).done(function(){
            //     alert('ok');
            // }).fail(function(e){
            //     alert('fail '+JSON.stringify(e));
            // });
            // window.location.href="/orders";
        });

        $('div.product-list-card-body').on('click', function (e) { // TODO: id, name 은 어떻게 전달하지 ?
            // console.log('이전 페이지는 '+window.location.href.);
            // let beforeProductId = $('#input-product-id').val();
            // console.log('이전 페이지 id는 '+beforeProductId);

            var productId = this.children[0].value; // TODO 확인!
            $.ajax({
                type: 'GET',
                url: '/api/click/' + productId,
                dataType: 'json',
                contentType: 'application/json; charset=utf-8'
            }).done(function () { // 왜 안되지 ?
                console.log('ok');
                // _this.clickItem(productId);
            }).fail(function (e) {
                console.log('fail ' + JSON.stringify(e));
            });

            console.log(productId);
            // _this.click(productId, productName);
            _this.clickItem(productId); // TODO 주석 해제
        });

        $('#btn-search').on('click', function (e) {

            // var productId = this.children[0].value; // TODO 확인!
            var keyword = $('#input-search-keyword').val();
            // var keyword = this.parent("div").parent("div").children[0].value;

            $.ajax({
                type: 'GET',
                url: '/api/search?keyword=' + keyword,//$("input#input-cart-order").val(),
                dataType: 'json',
                contentType: 'application/json; charset=utf-8'
            }).done(function () { // 왜 안되지 ?
                alert('ok');
            }).fail(function (e) {
                alert('fail ' + JSON.stringify(e));
            });
        });

        $('div.product-list-card-body').on('mouseenter', function (e) {


            let productId = this.children[0].value; // TODO 확인!
            console.log('hover ' + productId);

            $.ajax({
                type: 'GET',
                url: '/api/hover/' + productId,
                dataType: 'json',
                contentType: 'application/json; charset=utf-8'
            }).done(function () { // 왜 안되지 ?
                console.log('ok');
            }).fail(function (e) {
                console.log('fail ' + JSON.stringify(e));
            });

        });

    },
    clickItem: function (id) {
        window.location.href = '/products/' + id;
    }
};

userAction.init();