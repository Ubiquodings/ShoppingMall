$(document).ready(function () {
    init();
});

function init() {
    var sell_price;
    var amount;
    //var init = 0;
    var form_list = document.getElementsByClassName("formlist"); // 잘나옴
    console.log("form_list length " + form_list.length);
    /*var amount_list = document.getElementsByClassName("amount");
    console.log("amount length " + amount_list.length); //이거도 수만큼 잘 나오네...*/

    /*var shopListId_list = document.getElementsByClassName("cart-item-id");
    console.log("shoplistId length " + shopListId_list.length);*/ // 이거도 수만큼 잘나온다

    /*var test = 0;*/
    var totalSum = 0; // 채민: 임의대로 잠시 test -> totalSum으로 바꿔서 해볼게요!
    var init_list = $(".init_c"); // 테이블에 번호를 주어서 form의 index를 가져와볼려고 시도..
    if (form_list.length == 1) { // list에 하나 있을때는 따로 처리해줘야 하는듯.. 잘 안나온다.
        sell_price = document.form.sell_price.value;
        amount = document.form.amount.value;
        document.form.sum.value = sell_price;
        /*$('.init_c').val('0'); 그냥 1부터 시작하게 하는게...*/
        $('.init_c').val('1');
        hm = document.form.amount;
        sum = document.form.sum;
        if (hm.value < 0) {
            hm.value = 0;
        }
        sum.value = parseInt(hm.value) * sell_price;
        totalSum = sum.value;
        document.getElementById("allsum").value = totalSum;
    } else {
        for (var i = 0; i < form_list.length; i++) {
            sell_price = document.form[i].sell_price.value;
            amount = document.form[i].amount.value;
            document.form[i].sum.value = sell_price;
            /*console.log("i " + i); 확인! 잘 나온다.*/
            $(init_list[i]).val(i + 1); // 0번째부터 나와서 그냥 +1 해줬다.
            hm = document.form[i].amount;
            sum = document.form[i].sum;
            if (hm.value < 0) {
                hm.value = 0;
            }
            sum.value = parseInt(hm.value) * sell_price;
            /**
             * 윤진언니 총 합계 수량 메소드
             * */
            /*if(parseInt(sum.value)){
                test += parseInt(sum.value);
            }*/
            totalSum = totalSum + parseInt(sum.value);
        }
        /*document.getElementById("allsum").value = test;*/
        document.getElementById("allsum").value = totalSum;
    }
}

$('.minus-btn').on('click', function () {
    var form_list = document.getElementsByClassName("formlist");
    if (form_list.length == 1) {
        console.log("minus button click alone");
        var sell_price = document.form.sell_price.value;
        hm = document.form.amount;
        sum = document.form.sum;
        if (hm.value > 1) {
            hm.value--;
            sum.value = parseInt(hm.value) * sell_price;
            //원래 totalSum에다가ㅏ sell_price를 빼면 되지 않을까?
            // try 해보자..ㅎ
            var totalSum = document.getElementById("allsum").value;
            console.log("minus before totalSum " + totalSum);
            totalSum = totalSum - sell_price;
            document.getElementById("allsum").value = totalSum;
            console.log("minus after totalSum " + totalSum);
        }
        /**
         * 윤진언니 총 합계 수량 메소드
         * */
        /*if(parseInt(sum.value)){
            test -= parseInt(sum.value);
        }
        document.getElementById("allsum").value = test;*/
    } else { // length 가 1 아님
        console.log("minus button click");
        var minus_index = $('.minus-btn').index($(this));
        console.log("minus_index " + minus_index);
        var sell_price = document.form[minus_index].sell_price.value;
        hm = document.form[minus_index].amount;
        sum = document.form[minus_index].sum;
        if (hm.value > 1) {
            hm.value--;
            sum.value = parseInt(hm.value) * sell_price;
            //원래 totalSum에다가ㅏ sell_price를 빼면 되지 않을까?
            // try 해보자..ㅎ
            var totalSum = document.getElementById("allsum").value;
            console.log("minus before totalSum " + totalSum);
            totalSum = totalSum - sell_price;
            document.getElementById("allsum").value = totalSum;
            console.log("minus after totalSum " + totalSum);
        }
        /**
         * 윤진언니 총 합계 수량 메소드
         * */
        /*<!--이 밑에 함수가 안먹히는듯-->
        if(parseInt(sum.value)){
            test -= parseInt(sum.value);
        }
        document.getElementById("allsum").value = test;*/
    }
});
$('.plus-btn').on('click', function () {
    var form_list = document.getElementsByClassName("formlist");
    if (form_list.length == 1) {
        console.log("plus button click alone");
        var sell_price = document.form.sell_price.value;
        hm = document.form.amount;
        sum = document.form.sum;
        hm.value++;
        sum.value = parseInt(hm.value) * sell_price;
        var totalSum = document.getElementById("allsum").value;
        console.log("plus before totalSum " + totalSum);
        totalSum = parseInt(totalSum) + parseInt(sell_price);
        document.getElementById("allsum").value = totalSum;
        console.log("plus after totalSum " + totalSum);
    } else {
        console.log("plus button click");
        var plus_index = $('.plus-btn').index($(this));
        console.log("plus_index " + plus_index);
        var sell_price = document.form[plus_index].sell_price.value;
        hm = document.form[plus_index].amount;
        sum = document.form[plus_index].sum;
        hm.value++;
        sum.value = parseInt(hm.value) * sell_price;
        var totalSum = document.getElementById("allsum").value;
        console.log("plus before totalSum " + totalSum);
        totalSum = parseInt(totalSum) + parseInt(sell_price);
        document.getElementById("allsum").value = totalSum;
        console.log("plus after totalSum " + totalSum);
    }
});

/**
 * 장바구니 취소 로직 3개.
 * userAction.js로 옮겼다.
 */
//1. id값으로 하니까 안된다... id값은 하나인걸 까먹었고.. index값이 안구해지는건데..바보..
/*$('#btn-cart-cancel').on('click', function(e) {

    var form_list = document.getElementsByClassName("formlist");

    console.log("cart cancel button formlist 길이 " + form_list.length);

    if (form_list.length == 1) {
        console.log("delete button click alone");
        /!*var shopListId = document.getElementsByClassName("cart-item-id").value;*!/
        //위에처럼 하면 안되고 밑에처럼 [0]을 해줘야한다.
        var shopListId = document.getElementsByClassName("cart-item-id")[0].value;
        /!*var shopListId = document.getElementsByClassName("cart-item-id").val();*!/
        console.log("shopListId " + shopListId);

        /!*$.ajax({
            type: 'DELETE',
            url: '/api/carts/'+ shopListId, // order id
            dataType: 'json',// Accept ?
            contentType:'application/json; charset=utf-8',
            // data: JSON.stringify({})
        }).done(function() {
            alert('장바구니에서 삭제되었습니다.');
            window.location.href=window.location.href;
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });*!/
    }
    else {
        console.log("상품 여러개 delete button click");
        var shopListId_List = document.getElementsByClassName("cart-item-id"); // 여러개니까
        console.log("상품 여러개일때 shopListId length " + shopListId_List.length);

        //delete 버튼의 index를 가져온다.
        var delete_index = $('#btn-cart-cancel').index($(this)); // 왜 index 0만 되는거지..
        //var delete_index = $(this).index(); // 엥 첫번째 요소가 index 1나온다.

        var shopListId = shopListId_List[delete_index].value;

        console.log("delete index " + delete_index);
        console.log("shopListId " + shopListId);

        /!*$.ajax({
            type: 'DELETE',
            url: '/api/carts/'+ shopListId, // order id
            dataType: 'json',// Accept ?
            contentType:'application/json; charset=utf-8',
            // data: JSON.stringify({})
        }).done(function() {
            alert('장바구니에서 삭제되었습니다.');
            window.location.href=window.location.href;
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });*!/
    }
});*/

//2. class로 해서 다시 했다. 생각해보니 formlist가 하나일때랑 상관 없는거 같아서 이걸 지웠다.
/*$('.btn-cart-cancel').on('click', function(e) {

    var form_list = document.getElementsByClassName("formlist");

    console.log("cart cancel button formlist 길이 " + form_list.length);

    if (form_list.length == 1) {
        console.log("delete button click alone");
        /!*var shopListId = document.getElementsByClassName("cart-item-id").value;*!/
        //위에처럼 하면 안되고 밑에처럼 [0]을 해줘야한다.
        var shopListId = document.getElementsByClassName("cart-item-id")[0].value;
        /!*var shopListId = document.getElementsByClassName("cart-item-id").val();*!/
        console.log("shopListId " + shopListId);

        /!*$.ajax({
            type: 'DELETE',
            url: '/api/carts/'+ shopListId, // order id
            dataType: 'json',// Accept ?
            contentType:'application/json; charset=utf-8',
            // data: JSON.stringify({})
        }).done(function() {
            alert('장바구니에서 삭제되었습니다.');
            window.location.href=window.location.href;
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });*!/
    }
    else {
        console.log("상품 여러개 delete button click");
        var shopListId_List = document.getElementsByClassName("cart-item-id"); // 여러개니까
        console.log("상품 여러개일때 shopListId length " + shopListId_List.length);

        //delete 버튼의 index를 가져온다.
        var delete_index = $('.btn-cart-cancel').index($(this)); // 왜 index 0만 되는거지..
        //var delete_index = $(this).index(); // 엥 첫번째 요소가 index 1나온다.

        var shopListId = shopListId_List[delete_index].value;

        console.log("delete index " + delete_index);
        console.log("shopListId " + shopListId);

        /!*$.ajax({
            type: 'DELETE',
            url: '/api/carts/'+ shopListId, // order id
            dataType: 'json',// Accept ?
            contentType:'application/json; charset=utf-8',
            // data: JSON.stringify({})
        }).done(function() {
            alert('장바구니에서 삭제되었습니다.');
            window.location.href=window.location.href;
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });*!/
    }
});*/

//3. formlist 상관 없이 index 구해지게!
$('.btn-cart-cancel').on('click', function(e) {

    console.log("delete button click");
    var shopListId_List = document.getElementsByClassName("cart-item-id");
    console.log("shopListId length " + shopListId_List.length);

    //delete 버튼의 index를 가져온다.
    var delete_index = $('.btn-cart-cancel').index($(this)); // 왜 index 0만 되는거지..

    var shopListId = shopListId_List[delete_index].value;

    console.log("delete index " + delete_index);
    console.log("shopListId " + shopListId);

    $.ajax({
        type: 'DELETE',
        url: '/api/carts/'+ shopListId, // order id
        dataType: 'json',// Accept ?
        contentType:'application/json; charset=utf-8',
        // data: JSON.stringify({})
    }).done(function() {
        alert('장바구니에서 삭제되었습니다.');
        window.location.href=window.location.href;
    }).fail(function (error) {
        alert(JSON.stringify(error));
    });

});

function change() {
    var form_list = document.getElementsByClassName("formlist");
    if (form_list.length == 1) {
        var sell_price = document.form.sell_price.value;
        var amount = document.form.amount.value;
        document.form.sum.value = sell_price;
        hm = document.form.amount;
        sum = document.form.sum;
        if (hm.value < 0) {
            hm.value = 0;
        }
        sum.value = parseInt(hm.value) * sell_price;
        /**
         * 윤진언니 총 합계 수량 메소드
         * */
        <!--이 밑에 함수가 안먹히는듯-->
        /*for (var i = 0; i < form_list.length; i++){
            if(parseInt(sum.value)){
                test += parseInt(document.form[i].sum.value);
            }
            document.getElementById("allsum").value = test;
        }*/
        <!-- 채민: 이게 지금 하나라서 아마 document.form[i]가 안먹힐거에요 ㅜㅜ-->
        document.getElementById("allsum").value = sum.value;
        console.log("change method after totalSum " + sum.value);
    } else {
        var change_index = $('.amount').index();
        console.log(change_index);
        var sell_price = document.form[change_index].sell_price.value;
        var amount = document.form[change_index].amount.value;
        document.form[change_index].sum.value = sell_price;
        hm = document.form[change_index].amount;
        sum = document.form[change_index].sum;
        if (hm.value < 0) {
            hm.value = 0;
        }
        sum.value = parseInt(hm.value) * sell_price;
        /**
         * 윤진언니 총 합계 수량 메소드
         * */
        <!--이 밑에 함수가 안먹히는듯-->
        /*for (var i = 0; i < form_list.length; i++){
            if(parseInt(sum.value)){
                test += parseInt(document.form[i].sum.value);
            }
            document.getElementById("allsum").value = test;
        }*/
        var totalSum = 0;
        for (var i = 0; i < form_list.length; i++) {
            totalSum = parseInt(totalSum) + parseInt(document.form[i].sum.value);
        }
        document.getElementById("allsum").value = totalSum;
        console.log("change method after totalSum " + totalSum);
    }
}

