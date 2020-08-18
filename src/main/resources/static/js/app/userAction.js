var userAction = {
    init: function(){
        var _this = this;

        $("#btn-order-cancel").on('click',function(e){ // detail 페이지에서 가져와야지
            // var productId = this.children[0].value;
            $.ajax({
                type: 'DELETE',
                url: '/api/orders/'+ $("input#input-order-id").val(), // order id
                dataType: 'json',// Accept ?
                contentType:'application/json; charset=utf-8',
                // data: JSON.stringify({}) // TODO 간단하게 url param 으로 대체
            }).done(function() {
                alert('삭제되었습니다.');
                window.location.href=window.location.href;
            }).fail(function (error) {
                alert(JSON.stringify(error));
            });
            // .done(function(){ // 왜 안되지 ?
            //     alert('ok');
            // });
            // window.location.href=window.location.href;
        });


        $(".btn-shoplist").on('click',function(e){ // detail 페이지에서 가져와야지
            var productId = this.children[0].value;
            //var count = this.children[1].value;
            var count = $('#count').val();
            $.ajax({
                type: 'POST', // 일단 상품 list 에서 가능한지 확인 후 detail 수정
                url: '/api/carts/new/'+ productId,//$("input#product-detail-id").val(),
                dataType: 'json',
                contentType:'application/json; charset=utf-8',
                //data: JSON.stringify({}) // TODO 간단하게 url param 으로 대체
                data: JSON.stringify(count)
            }).done(function(){ // 왜 안되지 ?
                alert('ok');
            }).fail(function(e){
                alert('fail '+JSON.stringify(e));
            });
            // window.location.href=window.location.href; // reloading ?
        });
        // button 에 input 넣기
        // button class 수정
        $(".btn-order").on('click', function(e){ // detail 페이지에서 가져와야지
            // console.log('ajax 전');
            var productId = this.children[0].value;
            $.ajax({
                type: 'POST',
                url: '/api/orders/new/'+ productId,//$("input#input-cart-order").val(),
                dataType: 'json',
                contentType:'application/json; charset=utf-8',
                data: JSON.stringify({
                    // productId:
                }) // TODO 간단하게 url param 으로 대체
            }).done(function(){ // 왜 안되지 ?
                alert('ok');
            }).fail(function(e){
                alert('fail '+JSON.stringify(e));
            });
            // console.log('ajax 후');
            // window.location.href=window.location.href; // reloading ?
        });
        $('div.product-list-card-body').on('click', function(e){ // TODO: id, name 은 어떻게 전달하지 ?
            // console.log('이전 페이지는 '+window.location.href.);
            // let beforeProductId = $('#input-product-id').val();
            // console.log('이전 페이지 id는 '+beforeProductId);

            var productId = this.children[0].value; // TODO 확인!
            $.ajax({
                type: 'GET',
                url: '/api/click/'+ productId,
                dataType: 'json',
                contentType:'application/json; charset=utf-8'
            }).done(function(){ // 왜 안되지 ?
                console.log('ok');
                // _this.clickItem(productId);
            }).fail(function(e){
                console.log('fail '+JSON.stringify(e));
            });

            console.log(productId);
            // _this.click(productId, productName);
            _this.clickItem(productId); // TODO 주석 해제
        });

        $('#btn-search').on('click', function(e){

            // var productId = this.children[0].value; // TODO 확인!
            var keyword = $('#input-search-keyword').val();
            // var keyword = this.parent("div").parent("div").children[0].value;

            $.ajax({
                type: 'GET',
                url: '/api/search?keyword='+ keyword,//$("input#input-cart-order").val(),
                dataType: 'json',
                contentType:'application/json; charset=utf-8'
            }).done(function(){ // 왜 안되지 ?
                alert('ok');
            }).fail(function(e){
                alert('fail '+JSON.stringify(e));
            });
        });

        $('div.product-list-card-body').on('mouseenter', function(e){


            let productId = this.children[0].value; // TODO 확인!
            console.log('hover '+productId);

            $.ajax({
                type: 'GET',
                url: '/api/hover/'+ productId,
                dataType: 'json',
                contentType:'application/json; charset=utf-8'
            }).done(function(){ // 왜 안되지 ?
                console.log('ok');
            }).fail(function(e){
                console.log('fail '+JSON.stringify(e));
            });

        });

    },
    clickItem:function(id){
        window.location.href = '/products/'+id;
    }
};

userAction.init();