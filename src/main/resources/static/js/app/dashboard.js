// 서버에서 string 으로 리턴하면 parse 바로 하면 되는데
// json 으로 리턴하면 stringify 하고 parse 해야하는듯 !
// ...? 출력할 때는 stringify 해야 휴먼리더블로 출력된다

var dashboard = {
    // TODO ajax 가 socket 보다 먼저 실행되서 원하는대로 동작하지만,
    // TODO ajax 후 socket 연결이 되도록 뭔가 추가 세팅이 필요할거같아
    init: function () {
        let _this = this;
        $(document).ready(function () { // 문서가 준비되면
            _this.getUserCfRecommendationList();
            _this.getItemCfRecommendationList();
            _this.getFreqRecommendationList();
            _this.getDiscountRecommendationList();
        });
    },
    getUserCfRecommendationList: function () { // ?page=0
        let _this = this;
        let divId = "dashboard-recommendation-userCf";

        $.ajax({
            type: 'GET',
            url: '/api/recommendations/tmp?page=0', // TODO /api/recommendations/userCf
            dataType: 'json',
        }).done(function(result) {
            // 결과 파싱
            let resultString = JSON.stringify(result);
            // console.log('resultString: '+resultString);
            let productList = JSON.parse(resultString);
            // 배열 길이가 0이면 다시 이 함수 호출하기 !

            // 화면 준비
            let userCfRecommendationUI = document.querySelector('#'+divId);
            userCfRecommendationUI.innerHTML = "";

            Array.from(productList).forEach((row) => {
                // let product = JSON.parse(JSON.stringify(row.productId));
                console.log('가져온 product: '+JSON.stringify(row.productId));

                userCfRecommendationUI.innerHTML += _this.buildProductItemHTML(
                    row.productId, row.productImgUrl, row.productName,
                    row.productPrice, row.productDescription,
                    row.tagNameList, row.categoryName, row.categoryId);
            });

        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    },
    getItemCfRecommendationList: function () { // ?page=1
        let _this = this;
        let divId = "dashboard-recommendation-itemCf";

        $.ajax({
            type: 'GET',
            url: '/api/recommendations/tmp?page=1', // TODO /api/recommendations/userCf
            dataType: 'json',
        }).done(function(result) {
            // 결과 파싱
            let resultString = JSON.stringify(result);
            // console.log('resultString: '+resultString);
            let productList = JSON.parse(resultString);
            // 배열 길이가 0이면 다시 이 함수 호출하기 !

            // 화면 준비
            let userCfRecommendationUI = document.querySelector('#'+divId);
            userCfRecommendationUI.innerHTML = "";

            Array.from(productList).forEach((row) => { // js 객체일듯
                // let product = JSON.parse(JSON.stringify(row.productId));
                console.log('product: '+JSON.stringify(row.productId));

                userCfRecommendationUI.innerHTML += _this.buildProductItemHTML(
                    row.productId, row.productImgUrl, row.productName,
                    row.productPrice, row.productDescription,
                    row.tagNameList, row.categoryName, row.categoryId);
            });
            // userCfRecommendationUI.innerHTML += `<script src="/js/app/userAction.js"></script>`;

        }).fail(function (error) {
            alert(JSON.stringify(error));
        });

    },
    getFreqRecommendationList: function () { // ?page=2
        let _this = this;
        let divId = "dashboard-recommendation-freq";

        $.ajax({
            type: 'GET',
            url: '/api/recommendations/tmp?page=2', // TODO /api/recommendations/userCf
            dataType: 'json',
        }).done(function(result) {
            // 결과 파싱
            let resultString = JSON.stringify(result);
            // console.log('resultString: '+resultString);
            let productList = JSON.parse(resultString);
            // 배열 길이가 0이면 다시 이 함수 호출하기 !

            // 화면 준비
            let userCfRecommendationUI = document.querySelector('#'+divId);
            userCfRecommendationUI.innerHTML = "";

            Array.from(productList).forEach((row) => { // js 객체일듯
                // let product = JSON.parse(JSON.stringify(row.productId));
                console.log('product: '+JSON.stringify(row.productId));

                userCfRecommendationUI.innerHTML += _this.buildProductItemHTML(
                    row.productId, row.productImgUrl, row.productName,
                    row.productPrice, row.productDescription,
                    row.tagNameList, row.categoryName, row.categoryId);
            });

        }).fail(function (error) {
            alert(JSON.stringify(error));
        });

    },
    getDiscountRecommendationList: function () { // ?page=3
        let _this = this;
        let divId = "dashboard-recommendation-discount";

        $.ajax({
            type: 'GET',
            url: '/api/recommendations/tmp?page=3', // TODO /api/recommendations/userCf
            dataType: 'json',
        }).done(function(result) {
            // 결과 파싱
            let resultString = JSON.stringify(result);
            // console.log('resultString: '+resultString);
            let productList = JSON.parse(resultString);
            // 배열 길이가 0이면 다시 이 함수 호출하기 !

            // 화면 준비
            let userCfRecommendationUI = document.querySelector('#'+divId);
            userCfRecommendationUI.innerHTML = "";

            Array.from(productList).forEach((row) => { // js 객체일듯
                // let product = JSON.parse(JSON.stringify(row.productId));
                console.log('product: '+JSON.stringify(row.productId));

                userCfRecommendationUI.innerHTML += _this.buildProductItemHTML(
                    row.productId, row.productImgUrl, row.productName,
                    row.productPrice, row.productDescription,
                    row.tagNameList, row.categoryName, row.categoryId);
            });

        }).fail(function (error) {
            alert(JSON.stringify(error));
        });

    },
    getTagListHTML: function(tagNameList){
        // <!--태그 출력 ProductTagList List<ProductTag>-->
        let tagHTMLList = ``;
        Array.from(tagNameList).forEach((tag)=>{
            tagHTMLList += `<span class="badge badge-warning"
        style="background-color:pink">${tag} </span>`
        });

        return tagHTMLList;
    },

    buildProductItemHTML: function (id, imgUrl, name, price, description, tagNameList, categoryName, categoryId) {
        //<!-- id, imgUrl, name, price, description, tagNameList, categoryName, categoryId  -->
        let _this = this; // onclick="window.location.href = '/products/${id}';"    /products/${id}
        return `
        <div class="col-md-3 " >
            <div class="ibox">
                <div class="ibox-content product-box">

                    <div class="product-list-card-body" >
                        <input type="hidden" value="${id}" class="product-detail-id"/>

                        <!--이미지-->
                        <div class="product-imitation">
                            <img src="${imgUrl}"/> <!--[ 사진 ]-->
                        </div>

                        <div class="product-desc">
                            <!-- <span class="product-discount">save 10%</span>-->
                            <a href="#" class="product-name">${name}</a>
                            <small class="product-price">${price}</small>
                            <div class="small m-t-xs description">
                                ${description}
                            </div>

                            <div>`+ _this.getTagListHTML(tagNameList)
                            +`</div>
                            <div> <!--카테고리 이름-->
                                <span class="badge badge-secondary"
                                      style="background-color:darkorange">${categoryName}</span>
                                <span class="badge badge-secondary"
                                      style="background-color:darkorange">${categoryId}</span>
                            </div>
                        </div>
                    </div>

                    <div class="m-t row">
                        <div class="col-4">
                            <button type="button" style="margin-left:10px"
                                    class="btn btn-sm btn-outline btn-success btn-shoplist" id="btn-shoplist"
                                    data-toggle="modal" data-target="#exampleModal">

                                <input type="hidden" value="${id}" id="product-detail-id"/>

                                <input type="hidden" id="count" value="1"/>
                                <span class="material-icons white00">shopping_cart</span>
                            </button>
                        </div>

                        <!--아이콘2-->
                        <!--함께 보고있는 사용자 수-->
                        <div class="col-4 list-mini-text-font productUserNumber-${id}"
                             id="productUserNumber-${id}" style="padding:0; margin: 0">
                            <span class="material-icons light-green d-inline-block" tabindex="0" data-toggle="tooltip"
                                      title="이 상품을 함께 보고있는 사용자 수">visibility</span> <span
                                class="align-text-bottom">1명</span>
                        </div>

                        <!--구매한 사용자 수-->
                        <div class="col-4 list-mini-text-font productOrderUserNumber-${id}"
                             id="productUserNumber-${id}" style="padding:0; margin: 0">
                            <span class="material-icons light-green d-inline-block" tabindex="0" data-toggle="tooltip"
                                      title="이 상품을 구매한 사용자 수">payment</span> <span class="align-text-bottom">0명</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>`;
    }
};

dashboard.init();