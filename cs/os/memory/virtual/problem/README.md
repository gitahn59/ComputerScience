# 메모리 사용의 문제점
## Allocation of Frames
1. Fixed space algorithms :  사용가능한 페이지에 제한을 둠      
제한에 도달하면 그 프레임안에서 Replacement 해야 함  
다른 프로세스가 page를 적게 사용해 공간이 남아있더라도 사용할 수 없음

2. Variable space algorithms : 
그냥 글로벌하게 페이지를 replacement하는 방식, 다른 프로세스에 의해 다른 프로세스의 영향을 미칠 수 있음

## Thrashing
프로세스를 계속 띄우면 CPU 사용량이 올라간다.     
하지만 프로세스가 너무 많아 page fault 비율이 증가하면 아무 작업도 못하고 CPU 사용량이 급격히 감소한다.

## Working Set
working set : 현제 프로세스가 동작하는데 필요한 집합