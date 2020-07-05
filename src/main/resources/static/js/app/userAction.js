var userAction = {
    init: function(){
        var _this = this;

        $("#btn-order-cancel").on('click',function(e){ // detail 페이지에서 가져와야지
            // var productId = this.children[0].value;
            $.ajax({
                type: 'DELETE',
                url: '/orders/'+ $("input#input-order-id").val(), // order id
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
            $.ajax({
                type: 'POST', // 일단 상품 list 에서 가능한지 확인 후 detail 수정
                url: '/carts/new/'+ productId,//$("input#product-detail-id").val(),
                dataType: 'json',
                contentType:'application/json; charset=utf-8',
                data: JSON.stringify({}) // TODO 간단하게 url param 으로 대체
            }).done(function(){ // 왜 안되지 ?
                alert('ok');
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
                url: '/orders/new/'+ productId,//$("input#input-cart-order").val(),
                dataType: 'json',
                contentType:'application/json; charset=utf-8',
                data: JSON.stringify({
                    // productId:
                }) // TODO 간단하게 url param 으로 대체
            }).done(function(){ // 왜 안되지 ?
                alert('ok');
            });
            // console.log('ajax 후');
            // window.location.href=window.location.href; // reloading ?
        });
        $('div.product-list-card-body').on('click', function(e){ // TODO: id, name 은 어떻게 전달하지 ?

            /*
            product name:  this.children[0].innerHTML
            product price: this.children[1].innerHTML
            product list index: this.children[2].value
            */
            // var productName = this.children[0].innerHTML;
            // var productPrice = this.children[1].innerHTML;
            // var productListIndex = this.children[2].value;// 상품 하드코딩하니까.. - 1;
            var productId = this.children[0].value; // TODO 확인!
            // var productId = this.children[0].value;
            $.ajax({
                type: 'GET',
                url: '/click/'+ productId,//$("input#input-cart-order").val(),
                dataType: 'json',
                contentType:'application/json; charset=utf-8'
                // data: JSON.stringify({ // data 안지우면 get 요청이 안되는군!
                //     // productId:
                // })
            }).done(function(){ // 왜 안되지 ?
                alert('ok');
            });

            console.log(productId);
            // _this.click(productId, productName);
            _this.clickItem(productId);
        });

    },
    clickItem:function(id){
        window.location.href = '/products/'+id;
    }
};

userAction.init();