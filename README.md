



# api

- ```json
  # 예시
  
  ## url
  ## request
  
  # '{보안}'은 구글 인증해야 접속 가능
  ```

- ```json
  # 화면 관련
  
  ## 메인 페이지
  /
  
  ## 주문 목록 {보안}
  /orders
  
  ## 주문 상세 {보안} -- 필요없을지도
  /orders/{id}
  
  ## 상품 목록
  /products
  
  ## 상품 상세
  /products/{id}
  
  ## 장바구니 목록 {보안}
  /carts
  
  ## 마이페이지 {보안}
  /mypage
  ```

- 

- ```json
  # 상품 생성 api
  ## url
  /products/new
  
  ## request
  ```

- ```json
  # 장바구니 저장 {보안} -- id: productId
  /carts/new/{id}
  ```

- ```json
  # 주문 저장 {보안} -- id: productId
  /orders/new/{id}
  ```

- 

















































