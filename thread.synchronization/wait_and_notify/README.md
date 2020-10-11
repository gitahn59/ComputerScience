# wait와 notify를 이용한 동기화
> synchronized로 발생하는 장기간 대기에 의한 성능 저하를 방지하기 위해    
> wait와 notify를 통해 쓰레드를 동기화함.    

## 방법
1. 작업 진행 중 더 이상 진행을 할 수 없으면 wait()을 호출해 락을 반납하고 대기
2. 다른 쓰레드가 락을 얻어 해당 객체에 대한 작업을 수행
3. 나중에 작업을 진행할 수 있으면 notify()를 호출하여 임의의 대기중이던 쓰레드가 작업을 수행하도록 함 

## 단점
오래 기다린 쓰레드가 락을 얻는다는 보장이 없음.    
wait가 호출 되면 waiting pool에서 대기하며 통지를 기다린다.    
notify()가 호출되면, 해당 객체의 대기실에 있던 모든 쓰레드 중 임의의 쓰레드가 통지를 받는다.    
notifyAll()은 기다리고 있는 모든 쓰레드에게 통지를 하지만, 그래도 lock을 얻을 수 있는 쓰레드는 1개 뿐임.    
따라서 나머지 쓰레드는 다시 lock을 기다림

## wait

```java
void wait()
void wait(long timeout)
void wait(long timeout, int nanos)
```

1. wait()는 notify() 또는 notifyAll()이 호출될 때까지 기다린다.
2. 매개변수가 있는 경우는 지정된 시간동안만 기다리며, 시간이 지난 후에 자동적으로 진행한다.

## 공장 예제
1. Factory Thread는 "computer"와 "audio"를 생산해서 Stroage에 저장
2. Customer Thread는 Stroage에서 item을 구매, 
3. Stroage가 가득차있으면 Factory은 대기
4. Stroage가 비어있으면 Customer는 Factory의 물품 생산을 대기

동기화를 수행하지 않으면 ConcurrentModificationException 또는 IndexOutOfBoundsException이 발생할 수 있음.

### ConcurrentModificationException
> 어떤 쓰레드가 반복중에 Collection을 수정하는 경우 발생.    
> 공장 예제의 경우 공장이 아이템을 생산하는 도중에 remove를 수행하는 경우 발생

### IndexOutOfBoundsException
> A 쓰레드에서 물건을 제거하고, 다시 B 쓰레드에서 물건을 제거하는 과정에서    
> Collection의 인덱스가 충돌하여 발생 
                
### 1. Synchronized 키워드를 이용한 동기화
> 예외는 방지할 수 있지만, Customer가 Stroage의 Lock을 차지하고 계속해서 대기   
> 따라서 공장은 Stroage의 Lock을 얻지 못하고 무한정 대기하여 교착상태 발생

### 2. wait, notify를 이용한 동기화
> wait와 notify를 통해 Lock를 계속 차지하지 않도록 개선했지만,    
> 여전히 lock을 얻지 못하는 기아 현상의 가능성이 존재함.      
> 이 현상을 막기 위해 notifyAll()을 통해 모든 쓰레드에 통지할 수 있지만,   
> 불필요하게 Customer 쓰레드와 Factory 쓰레드의 경쟁하게 됨. => 경쟁 상태(race condition)

### 3. 개선
Lock과 Conditinon을 이용해 선별적인 notify 구현