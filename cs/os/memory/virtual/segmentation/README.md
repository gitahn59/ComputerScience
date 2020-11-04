# Segmentation
고정된 크기가 논리적인 크기로 유여한하게 관련있는 덩어리로 관리하는 기법    
1. variable-sized partition 기법의 확장 버전    
2. 세그먼트의 크기는 변화할 수 잇음

## Segment table 
paging 기법의 page table 역할, 크기가 유동적이기 때문에 limit 값을 추가적으로 보유

## 장점
1. 동적인 메모리르 관리하기 편함
2. segments를 보호하기 쉬움 : 코드, data를 구별하여 안전한 관리가 가능함
3. segments를 공유하기 쉬움

## 단점
1. Cross segment addresses
포인터를 이용한 주소 연산의 어려움 
2. 테이블을 관리하기 위한 메모리 소비
3. 외부 단편화

## 목적
논리적으로 격리된 주소영역을 구성하여 공유와 보호를 제공함

## 결론
Segmentation with Paging    
세그먼트 단위로 나누고 이를 다시 페이지 단위로 나누어서 사용한다.
디스크에서 세그먼트 단위가 아니라 세그먼트 안의 페이지 단위로 메모리에 올리고 내린다.
