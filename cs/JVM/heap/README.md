# Heap

## JAVA7 이하   
## Young Generation     
Eden, Survior 1, Survior 2 세 개의 영역으로 구분된다.
### Minior GC 과정
> 1. 객체가 생성되면 그 객체는 Eden 영역에 최초로 할당된다.   
> 2. Eden 영역이 가득 차면 Minior GC가 발생하여 Eden 영역과, from 영역의 객체를 to 영역으로 복사한다.   
> 3. to 영역으로 이동된 객체들의 age를 1개씩 증가시키고, Eden 영역과 from 영역을 비운다.   
> 4. 그러면 자연스럽게 to 영역에만 참조가 유지되는 객체들이 살아 남는다.
> 5. from, to가 가리키는 Survior 영역을 바꾼다.

## Old Generation
Young 에서 오래 살아남아 성숙된 객체는 Old Generation으로 이동한다.(기본 threshold = 31)   
비교적 오래 살아남은 객체는 다음에도 다시 사용할 확률이 높다. 이를 promotion이라고 한다.
이러한 promotion 과정 중 old generation의 메모리도 가득차면 
해당 영역에도 GC가 발생하며 이를 Full GC(Major)라고 한다.

## Perm
Class의 Meta 정보나, Method의 Meta 정보, Static 변수와 상수 정보들이 저장되는 공간
JAVA8부터는 Native 영역으로 이동하여 Metaspace 영역으로 변경되었다.

## GC의 문제점
명시적인 Memory 해제 보다 느리며 GC가 발생하는 순간 Suspend Time으로 인해 에러 발생 가능
일반적으로 GC를 튜닝하기 전에 애플리케이션 최적화(생성 객체 최소화)이후 튜닝을 진행한다.

## Reachability
root set : 객체의 참조 여부를 파악하기 위한 최초 객체의 참조 집합
객체에 유효한 참조가 있는 객체는 Reachable Object이다.    
- 힙 내의 다른 객체에 의한 참조
- Java stack(지역변수 영역, 메서드 파라미터, operand stack(계산을 위한 작업 공간))에 참조 정보가 있는 객체
- 메서드 영역에 로딩된 클래스 중 정적 변수에 의한 참조가 있는 객체 
- 아직 Memory에 남아있고 Native 스택에 의해, 즉 JNI(Java Native Interface)에 의해 생성된 객체에 대한 참조 객체
이 중에서 힙 내의 다른 객체에 의한 참조를 제외하면, root set으로 간주한다.

이 외의 객체들은 Unreachable Object로 간주되어 GC의 대상이 된다.

## Hotspot JVM의 GC 원리
1. 객체는 생성된 후 금방 Garbage가 된다.
> 새로 할당되는 Object가 모인 곳은 단편화될 확률이 높다.      
> 따라서 Minior GC를 통해 바로바로 제거하고 이동하면서 단편화를 방지한다.

2. 오래된 객체가 새로운 객체를 참조하는 일은 드물다.
> 따라서 드문 경우(Card Table 이용)만 추가적으로 확인하여 Young 영역의 참조 관계를 빠르게 추적할 수 있다.

Garbage 추적은 Tracing 알고리즘을 사용한다. 즉 Root Set에서 부터 참조 관계를 추적하여 Live Object를 Marking한다.
이러한 마킹작업은 Young Generation에 국한되는데 이 작업은 Memory Suspend 일떄 수행되기 때문이다.

전체 Heap을 대상으로한 마킹은 긴 시간이 필요하다. 만약 오래된 객체가 새로운 객체를 참조한다면 존재 여부를 체크하기 위해
Old Generation을 모두 찾아다녀야 한다. 이를 방지하기 위해 Card Table을 사용한다.

### Card Table
Old Generation의 별도 메모리 구조로, Young 객체를 참조하는 Old 객체가 있다면 Old객체의 시작주소에 카드를 표시하고 테이블에 기록한다.
이후 해당 참조가 해제되면 표시한 card도 사라지게 하여 참조 관계를 쉽게 파악한다.
Minor GC 수행 중에는 Card Table의 Dirty Card만 검색하면 빠르게 참조 관계를 파악할 수 있다.

### TLAB(Thread-Local Allocation Buffers)
GC가 발생하거나 객체가 각 영역에서 다른 영역으로 이동할 때 애플리케이션의 병목이 발생하여 성능에 영향을 준다.   
따라서 TLABs 라는 것을 사용한다. 이를 통해 각 스레드별 메모리 버퍼를 사용하면 다른 스레드에 영향 없이 
메모리 할당 작업이 가능하게 된다.

cf) bump-the-pointer : 할당된 메모리의 마지막의 다음 영역을 가리켜 연속된 빈 공간을 빠르게 할당하는 기술
- 객체 할당은 Bump-the-pointer 기술을 활용해 기존 할당된 메모리 번지 바로 다음부터 진행한다.
- JVM은 멀티 쓰레드이므로 바로 뒤 공간을 동시에 요청하면 동기화 이슈가 발생한다.
- TLAB는 Thread마다 할당 주소 범위를 부여하여 동기화 작업 ㅇ벗이 빠르게 부여한다.
- TLAB를 사용하지 않으면 Thread가 락을 걸고 다른 쓰레드는 Allocation을 대기하게 된다.

## GC의 대상과 범위
GC의 대상은 Young, Old Generation과 Permanent Area이며 각 영역에 각각 GC를 수행한다.
- Minor GC : Young Generation에서 빈번하게 발생하며 성숙된 객체를 Old Area로 promotion한다.
- Major GC : promotion이 누적되어 Old Generation의 메모리도 부족해지면 이 영역에 GC가 일어난다.
             Permanent Area의 Memory가 부족해도 GC가 발생하며 너무 많은 수의 Class Objact가 로딩되어
             빈공간이 부족할 때 발생한다.
Major GC는 Minor GC에 비하여 더 긴 Suspend 시간을 

## STW(Stop-The-World)
FUll GC를 수행할 때 JVM은 어플리케이션 실행을 중단하고 GC 쓰레드만 동작한다.
heap을 늘리면 FUll GC의 처음 발생 시점은 늦출 수 있지만, STW의 시간은 heap의 크기에 비례하므로
클 수록 더 많은 suspend 시간이 필요하다.

## GC 튜닝
생성된 객체는 GC가 자동으로 처리하고 지운다. 생성된 객체가 많을수록 GC가 처리해야 하는 대상이 많아지며
GC를 수행하는 횟수가 증가한다. 즉 기본적으로 객체를 줄이는 작업이 선행되어야 한다.
ex)
String 대신 StringBuffer(thread-safe), StringBuilder(synchronization이 적용되지 않아 빠름)를 사용해야 한다.
문자열을 변경하는 경우 String은 객체 자체의 복사 변경이 발생하여 Heap에 객체가 누적되는 반면,
StringBuilder나 StringBuffer는 내부적으로 객체가 가지고있는 값만 변경된다.
또한 로그를 최대한 적게 쌓도록 하는 것이 좋다.

### 목적
1. Old Areaㄹ 넘어가는 객체의 수 최소화
2. FUll GC의 실행 시간 최소화(GC가 길어지면 타임아웃 에러를 유발할 수 있음)
> Full GC 실행 시간을 줄이기 위해 Old Area의 크기를 줄이면 Out of memory 예외가 발생하거나 Full GC 횟수가 증가함
> Old Area의 크기를 증가시키면 Full GC 횟수는 감소하지만 실행 시간이 증가함
