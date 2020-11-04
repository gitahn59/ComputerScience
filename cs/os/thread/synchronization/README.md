# 동기화
크리티컬 섹션을 지원하는 기법
1. Locks
2. Semaphores
3. Monitors
4. Messages

## Locks
lock은 어떤 오브젝트(변수,객체)이다.
1. acquire() 는 lock이 락이 풀릴 때까지 기다리고 lock을 얻음 
2. release() : unlock

한 번에 한 쓰레드만 락을 보유할 수 있다.

lock의 구현

```java
struct lock {int held = 0;}

void acquire(struct lock *l{
    while(l->held);
    l->leld = 1;
}

void release(struct lock *l){
    l->leld = 0;
}
```
코드의 문제점 : lock을 구현한 코드 자체도 critical section으로 동작해야한다.   
따라서 atomic 하게 동작해야 한다.
1. software-only solutions
peterson's Algorithm
```java
int turn;
int interested[2];'
interested[0] = false;
interested[1]] = false;

void acquire(int process){}
    int other = 1-process;
    interested[process] = TRUE     
    turn = other;
    while(interested[other] && turn==other);
}

void release(int preocee){
    interested[process] = FALSE
}
```

2. hardware atomic instructions : automic한 동작 제공    
TestAndSet : 변수 할당  
SWAP : 값 바꾸기    

## Problems with Spinlocks
Spinlock : while 체크를 위해 cpu를 계속 소모하는 문제

## Disabling interrupts
인터럽트를 막아 automic 하게 동작할 수 있음
커널레벨에서만 가능, 멀티 프로세서인 경우 문제가 될 수 있음, 크리티컬 섹션이 길면 문제가 됨

## 고수준 동기화
while을 통한 스핀락은 cpu를 낭비한다.

고수준 동기화
1. 대기 방법 변화 : block되어있으면 cpu를 낭비하지 않는다.
2. 인터럽트 수용

## 세마포어
Wait() : (= P(), down()) 공유변수를 감소시킴, 세마포어가 오픈될때까지 블락 상태 유지      
Signal() : (= V(), up()) 값을 증가시키고, 다른 쓰레드가 CS에 들어갈 수 있또록 함      

블락킹을 통해 스핀하지 않음
wait()이 불리면,
1. 세마포어가 열려있으면, 쓰레드 진입
2. 세마포어가 닫혀있으면, block하고 queue에 들어가 대기       

signal()이 불리면,
1. 다른 쓰레드가 큐에서 대기하고 있으면, 그 쓰레드를 unblocked
2. 큐에 아무도 기다리고 있지 않으면, signal이 기록       

history를 가지고 있다고 말함. counter 가 0보다 작아지면 세마포어가 닫혀 없어진다.   

```java
typedef struct {
    int value; // 1 or N
    struct process *L;
} semaphore;

void wait (semaphore S) {
    S.value--;
    if (S.value < 0) {
        add this process to S.L;
        block (); // 시스템 콜 지원 필요
    }
}

void signal (semaphore S) {
    S.value++;
    if (S.value <= 0) {
        remove a process P from S.L;
        wakeup (P); // 시스템 콜 지원 필요
    }
}
```
wait와 signal 역시 critical sections이어야 한다.

### 세마포어의 종류
1. Binary semaphore(mutex)      
한번에 하나의 쓰레드만 동작 가능
      
2. Counting semaphore       
여러 개의 유닛이 cs에 진입 가능

Monitors : m