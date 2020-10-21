# Lock과 Condition을 이용한 동기화

> java.util.concurrent.locks 패키지에서 제공하는 lock클래스들을 이용해서도 동기화를 할 수 있다.   
> JDK1.5에서 추가되었으며 그 이전에는 synchronized블럭으로만 제공되던 동기화 기능을 보완하였다.

> 기존 synchronized블럭으로 동기화를 하면 자동으로 lock이 잠기고 풀려 편리하지만(예외가 발생해도 자동으로 풀림)
> 같은 메서드 내에서만 lock을 걸 수 있다는 제약이 있다.

## ReentrantLock

> 가장 일반적인 Lock. '재진입할 수 있는' Lock인 이유는 특정 조건에서 lock을 풀고   
> 나중에 다시 lock을 얻고 임계영역의 작업을 수행 할 수 있기 때문이다.

```java
ReentrantLock()
// if fair == true 가장 오래 기다린 쓰레드가 lock을 획득
ReentrantLock(boolean fair) 
```
fair를 사용하면 공정하게 처리할 수 있지만 확인 과정에서 성능 저하가 발생한다.
따라서 대부분 공정함보다 성능을 선택한다.

### lock 과 unlock
```java
void lock() // lock을 잠근다
void unlock() // lock을 해지한다
boolean isLocked() // lock이 잠겼는지 확인한다
```

ReentrantLock을 사용하는 경우 수동으로 lock을 잠그고 해제해야 한다.   
따라서 임계 영역 내에서 예외가 발생하거나, return문으로 빠져 나가면 lock이 풀리지 않을 수 있으므로   
try-finally 문을 사용해서 이를 방지하는 방법을 사용한다.

```java
lock.lock();
try{ 
    //임계영역
} finally{
    lock.unlock();
}   
```

### tryLock()
```java
void tryLock() // lock을 잠근다
void tryLock(long timeout, TimeUnit unit) throws InterruptedException 
```
tryLock()은 다른 쓰레드에 의해 lock이 걸려 있으면 lock을 기다리지 않거나, timueout만큼만 기다린다.   
lock()은 lock을 얻을 때까지 쓰레드를 블락시키므로 쓰레드의 응답성이 나빠질 수 있다.   
tryLock()을 이용하면 지정된 시간동안 lock을 얻지 못하면 다시 기다리거나, 포기할 건지 결정할 수 있게 할 수 있다.
timeout을 기다리는 중에는 sleep()와 마찬가지로 interrupt()로 예외를 발생시킬 수 있다.

### Condition()
wait()와 notify의 경우 쓰레드를 구분해서 통지하지 못한다는 단점이 있다.   
Condition은 이 문제를 해결하기 위해 사용한다. wait()의 경우 공유 객체의 waiting pool을 사용하지만,   
Condition의 경우 각 쓰레드를 위한 Condition을 만들어서 각각의 waiting pool을 별도로 생성하도록 한다.

> Condition은 이미 생성된 lock으로부터 생성한다.
```java
ReentrantLock lock = new ReentrantLock(); 
Condition forFactory = lock.newCondition();
Condition forCustomer = lock.newCondition();
```

Condition의 경우 await(), signal()을 사용한다.

 ## 공장 예제 
 1. Factory Thread는 "computer"와 "audio"를 생산해서 Stroage에 저장
 2. Customer Thread는 Stroage에서 item을 구매 
 3. Stroage가 가득차있으면 Factory은 대기
 4. Stroage가 비어있으면 Customer는 Factory의 물품 생산을 대기
 
> 생산과 구매에 같은 lock을 사용하도록 하여 쓰레드를 동기화한다.   
> Factory는 물품을 생산하면 Customer Condition에 signal을 보낸다.
> Customer는 물품을 구매하면 Factory Condition에 signal을 보낸다.

> wait & notify를 사용하면 공통된 공유 객체를 사용하여 발생하는 Customer와 Factory의 경쟁을 방지할 수 있다. 
> lock과 condition을 활용하면 특정 쓰레드에게 singal을 보냄으로서 기아 현상과 경쟁상태를 개선할 수 있다. 