# Replacement
앞으로 쓰이지 않을 페이지를 찾아 필요한 페이지로 교체하는 과정

## Optimal page replacement
가장 긴 시간동안 쓰지 않을 페이지를 교체한다. => 가장 낮은 fault rate

문제점     
실제 시간을 예측할 수 없음

## FIFO
가장 먼저들어온 페이지를 가장 먼저 내보냄
캐시가 증가한다고 해서 page fault가 감소하지는 않음

## LRU
Least Recently Used : 가장 오랬동안 안쓰인 페이지를 제거   
R bit를 사용해서 근사치를 사용하여 구현함

Counter-based approach
1. R이 0이면 counter를 증가시킨다
2. R이 1이면 counter를 0으로 바꾼다

### Second Chand LRU
FIFO와 비슷한 방식이며 기회를 추가적으로 주는 방식이다.    
원형규로 모델링하고 arm이 계속 이동하면서    
1. R이 0이면 victim으로 선정한다.
2. R이 1이면 R를 0으로 바꾼다
arm이 한바퀴 도는동안 참조가 있었다면 R이 다시 1로 설정되어 eviction 되지 않을 것이다.

## NRU
R 비트와 M비트를 같이 보는 방식 
0. 처음에는 R=0, M=0 class0
1. Read가 발생하면 R=1, M=0 class2
2. Write가 발생하면 R=1, M=1 class3
3. 주기적으로 interrupt에 의해 R=0으로 바꾼다 class1 or 0    
class가 낮은 페이지가 victim으로 선정된다

장점
1. 이해하기 쉬움
2. 구현하기 쉬움
3. 아주 optimal하지는 않지만 어느정도 이점이 있음

## LFU
Counting-based 페이지 대체 알고리즘
Least frequently used : 가장 적게 사용된 페이지를 교체하는 알고리즘
Most frequently used : 가장 많이 사용된 페이지를 교체하는 알고리즘     
> 카운터가 적으면 방금 올라온 것일 수 있음   
> 카운터가 높으면 더 이상 사용하지 않을 수도 있음


