## 프로젝트명: Kafka를 활용한 실시간 소비자 행동 패턴 분석 고객 맞춤형 쇼핑몰
팀원: 강채민, 김시현, 오윤진, 황유연 

### 소개
#### 1) 쇼핑몰부터 고객 로그분석까지 모든 과정을 새로 구축한 시스템
글로벌 쇼핑 시스템에서 실제 사용하는 Kafka 시스템을 기반으로 사용자 로그 분석과 패턴 분석을 직접 구현 했으며, 이를 검증하는 인터넷 쇼핑몰도 직접 구현해서 가상 사용자 시뮬레이션을 통해 상호관계와 1:1 맞춤 분석을 직접 수행한다.
#### 2) 빠른 속도의 편리한 쇼핑몰
코로나 19를 기점으로 소비의 중심이 온라인으로 이동 및 확대되는 상황에서 플랫폼 차별화가 중요해졌다. 구경 등 필요한 상품 구매만이 쇼핑몰 방문 동기가 아니다. 쇼핑몰의 상품들은 신상품/인기상품의 구경 등의 콘텐츠 요소로도 작용한다.
이와 같은 상황을 반영하기 위해 행동이 유사한 다른 고객들이 활동한 상품목록 노출하여, 다양한 유저보이스와 상품들에 명확한 구매동기를 제공한다. 또한 고객이 평균 3초 동안 한 화면에 머문다고 가정하고 이를 검증하기 위해 고객이 이탈하기 전 3초 이내에 추천목록을 노출하거나 쿠폰을 제공한다.
#### 3) 실시간 데이터 수집/분석 및 결과 제공
수집한 사용자의 행동 데이터를 기반으로 직접 구현한 분석 시스템을 4초 간격으로 주기적으로 동작시켜 갱신 결과를 저장한다. 사용자가 활동하는 쇼핑몰 화면에서 고객 이탈 전 3초 간격으로 노출할 상품 목록을 갱신한다. 또한 장바구니에 담아놓고 구매하지 않거나, 일정 상품만 구매하는 등 사용자의 행동과 구매 패턴을 파악하여 쿠폰을 제공한다. 이러한 데이터 수집/분석 파이프라인은 향후 보편화될 라이브 커머스에도 적용 가능하다.

- 개발배경 및 필요성
  - 구매자가 원하는 물건이 있지만 상품명을 알지 못하는 어려움은 쇼핑몰 사용자 사이에서 꾸준히 제기되어 왔다. 사용자의 다양한 상황을 예측하고 분석하며 구매자의 쇼핑 도우미로서의 역할을 통해 쇼핑몰 이용자의 입장에 한걸음 다가가 편리함을 높여주고자 한다. 원하는 상품만을 빠르게 찾아 구매 가능하도록 사용자의 빠른 물품 찾기를 돕는다. 기술적 측면에서는 수집되는 대용량 스트리밍 데이터를 안정적으로 분석할 수 있는 환경을 구축하고자 한다.

- 특징 및 장점
  - Kafka를 이용하여 유연하고 안정적인 빅데이터 파이프라인을 구성할 수 있다.
  - Kafka에서 수집된 데이터를 분산형 RESTful 검색 및 분석 엔진인 엘라스틱 서치에 저장하고, 엘라스틱 서치에 저장된 데이터 확인과 분석을 도와주는 키바나를 이용하여 수집된 데이터를 모니터링할 수 있다.
  - UserCF(User-based Collaborative Filtering)와 ItemCF(Item-based Collaborative Filtering)를 활용하여 개별 사용자를 위한 추천서비스를 제공할 수 있다.
  - Web Socket 을 이용하여 분석 결과를 서버에서 프론트로 즉시 전달하여 사용자의 행동에 고객 이탈 전 3초 이내에 피드백을 제공할 수 있다.
  - 실제 데이터를 가지고 검증함 : 1,000개의 아이템(인터넷 쇼핑몰을 통해 크롤링하고, 그 정보를 재가공해서 API를 이용하여 데이터베이스에 저장함)과 30명 동시 사용자 접속 환경 아래에서 15개의 룰이 정상적으로 동작하는 것을 확인하였음

- 구성도
![구성도](https://github.com/Ubiquodings/ShoppingMall/blob/master/%ED%9A%8C%EC%9D%98%EB%A1%9D/%EA%B2%B0%EA%B3%BC/%EA%B5%AC%EC%84%B1%EB%8F%84.png)

### 상세 기능
- [전체 기능 목록](https://github.com/Ubiquodings/ShoppingMall/blob/master/%ED%9A%8C%EC%9D%98%EB%A1%9D/%EA%B2%B0%EA%B3%BC/%EC%A0%84%EC%B2%B4%EA%B8%B0%EB%8A%A5%EB%AA%A9%EB%A1%9D.pdf)
- [화면별 세부 기능 (결과 화면 포함)](https://github.com/Ubiquodings/ShoppingMall/blob/master/%ED%9A%8C%EC%9D%98%EB%A1%9D/%EA%B2%B0%EA%B3%BC/%ED%99%94%EB%A9%B4%EB%B3%84%20%EC%84%B8%EB%B6%80%EB%82%B4%EC%9A%A9.pdf)
- [결과화면](https://github.com/Ubiquodings/ShoppingMall/issues/12)
  - 홈화면
  - 상품목록화면
  - 상품상세화면
  - 장바구니화면
  - 결제화면
  - 쿠폰목록화면
  - 주문목록화면

### [제작노력](https://github.com/Ubiquodings/ShoppingMall/blob/master/%ED%9A%8C%EC%9D%98%EB%A1%9D/%EA%B2%B0%EA%B3%BC/%EC%A0%9C%EC%9E%91%EB%85%B8%EB%A0%A5.pdf)
- 목차
  - 쇼핑몰을 직접 구축한 이유
  - 어떤 목적으로 Kafka를 선택했는가?
  - 어떤 목적으로 ElasticSearch를 선택했는가?
  - 어떤 목적으로 추천 알고리즘: UserCF & ItemCF를 선택했는가?
  - 어떤 과정으로 Web Socket를 선택했는가?
  - 가상 사용자 시뮬레이션

### [개발타임라인](https://github.com/Ubiquodings/ShoppingMall/blob/master/%ED%9A%8C%EC%9D%98%EB%A1%9D/%EA%B2%B0%EA%B3%BC/%EA%B0%9C%EB%B0%9C%ED%83%80%EC%9E%84%EB%9D%BC%EC%9D%B8.pdf)