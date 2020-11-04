# 가비지 컬렉션
Hotspot JVM은 기본적으로 Generation별로 다른 Garbage Collection 알고리즘을 선택할 수 있다.

Collector | 옵션 | Young| Old
---|---|---|---
Serial | -XX+UseSerialGC | Serial | Serial-Mark-Sweep-Compaction
Parallel | -XX+UseParalleGC | Parallel Scavenge | Serial-Mark-Sweep-Compaction
Parallel Compacting | -XX+UseParalleOldGC | Parallel Scavenge | Parallel-Mark-Sweep-Compaction
CMS | -XX+UseConcMarkSweepGC | Parallel | Concurrent Mark-Sweep
G1 | -XX+UseG1GC | Snapshot-At-The-Beginning(SATB)

## Serial GC(-XX:+UseSerialGC)
Young / Old Generation 모두 Single CPU를 사용한다. 즉 1개의 Thread를 가지고 수행한다.
Client JVM의 기본 Collector이며 현재는 거의 사용되지 않는다.

Young 영역에서는 eden과 srvivor 영역을 사용하는 Sreial 알고리즘 사용
Old 영역에서는 Mark-Sweep-Compaction 알고리즘을 사용
### Mark-Sweep-Compaction
1. Mark : 사용되지 않은 객체 마크
2. Sweep : 사용되지 않는 객체 제거
3. Compaction : 파편화된 메모리를 앞에서부터 채워나감
메모리 충돌을 방지하기 위해 suspend(STW) 현상이 발생한다.

## Parallel GC(-XX:+UseParallelGC)
Throughput GC라고도 한다. CPU가 대기상태로 남는 것을 최소화하여 Serial Collector와 달리 Young영역에서 콜렉션을 병렬로 처리한다.

Young 영역에서는 Parallel Copy 알고리즘 사용
Old 영역에서는 Mark-Sweep-Compaction 알고리즘을 사용
Eden, Survivor Area 의 live Object Copy 작업을 여러 Thread가 동시에 수행할 때는 Suspend 현상이 발생한다.
투입한 Resourcde 만큼 Suspend Time을 단축시킬 수 있으며, PLAB라는 Promotion Buffer를 마련하여
충돌을 방지한다.

### PLAB(Parallel Allocation Buffer)
Gc Thread가 Promotion시 Thread마다 Old 영역의 일정부분을 미리 할당받아 사용한다.
따라서 메모리 충돌이 발생하지 않으며, 이를 다사용하면 다시 새로운 버퍼를 할당한다.
하지만 Old 영역의 단편화는 막을 수 없다.
TLAB 4K단위, PLAB 1K 단위

## Parallel compacting = Parallel Old GC
Parallel GC와 비교하여 Old 영역의 CG 방식이 Mark-Summary-Compaction으로 변경하여 멀티쓰레드에 유리한 GC 방식이다.
Old Area의 GC 시간을 감소시켜 효율이 증가하지만, 몇몇 애플리케이션이 CPU를 점유하면 오히려 성능을 발위하지 못한다.
1. Mark(STW,M) : 살아있는 객체를 식별하여 표시한다.
Old 지역을 영역으로 균등하게 나눈다. 영역이 구분되면 GC들은 각각 영역별로 살아있는 객체를 마크한다.
이 떄 살아있는 객체들의 크기, 위치 정보가 갱신된다. 
2. Summary(S) : 이전 GC를 수행하여 컴팩션된 영역에 살아 있는 객체의 위치를 조사하는 단계
Mark단계의 결과를 대상으로 작업을 수행한다. 영역 단위이며 GC 쓰레드는 영역의 통계 정보로 각 영역의
밀도를 평가한다. 밀도(살아있는 객체의 비율)로 Dense prefix를 설정한다. 
prefix는 도달가능한 객체의 대부분을 차지하는 영역이 어디까지인가 구분하는 선으로 다음 단계에서 진행할 대상을 구별한다.

선의 왼쪽에 있는 영역은 이후 GC에서 제외된다.
오래 살아있는 객체들은 점점 왼쪽으로 이동되고, 그 영역은 Dense prefix를 통해 구별되어 결과적으로 GC의 소요시간을 감소시킨다.

3. Compact(STW,M) : 컴팩션을 수행한다.
각 쓰레드들이 영역을 할당받아 컴팩션을 수행한다. Compaction 작업은 가비지를 정리하고, 살아있는 객체를 왼쪽으로 이동시킨다.
작업은 

Summary 단계는 GC를 수행한 영역에 대하여 별도로 살아있는 객체를 식별한다.

## CMS Collector(Concurrent Mark & Sweep GC)
low-latency collecotr로 알려져 있으며 힙 메모리 영역의 크기가 클 때 적합하다.
Suspen 시간을 분산하여 응답시간을 개선한다. 자원의 여유가 있을 때 GC의 Pause Time을 줄이는 것이 목적이며
크기가 큰 오래 존재하는 객체가 있는 경우 가장 적합하다.
Young Aread에는 Parallel Copy 알고리즘을 사용한다. 
Old Area에는 Concurrent Mark-Sweep알고리즘을 사용한다.

1. Initial Mark : 싱글 쓰레드만 사용하며 클래스 로더에서 가장 가까운 객체들만 생존여부를 탐색한다. 따라서 STW의 시간이 매우 짧다.
2. Concurrent Mark : 싱글 쓰레드만 사용해서 방금 확인한 객체에서 참조하고 있는 객체들을 확인한다. GC와 Working 쓰레드가 동시에 수행된다.
3. Remark Phase : 멀티 쓰레드가 사용되며 애플리케이션이 중지된다. 이미 마크된 객체를 다시 추적하여 객체를 확정한다.
4. Concurrent Sweep : 싱글 쓰레드만 사용한다. 애플리케이션은 수행되고 최종적으로 판정된 Live 객체를 제외한 Garbage를 지운다.

이러한 단계로 GC가 진행되므로 stw 시간이 매우 짧다. 응답 속도가 중요할 때 CMS GC를 사용한다.
따라서 Low Latency GC라고 부른다.

반면, 메모리와 CPU를 더 요구하며, COmpaction 단계가 기본적으로 제공되지 않는다.
조각난 메모리가 많아져 Compaction 작업을 실행하면 다른 GC보다 STW 시간이 더 길어지므로
Compaction 작업이 얼마나 자주, 오래 수행되는지를 고려해야 한다.

## G1 GC(Garbage First)
G1은 물리적으로 Generation 구분을 없애고 전체 Heap을 1Mbytes 단위 리전으로 나눈다.
G1은 영역의 참조를 관리하기 위해 remember set을 만들어 사용하다.(heap의 5% 미만 크기)
1. 비어 있는 영역에 새로운 객체 할당
2. 쓰레기가 쌓여서 꽉 찬 영역을 우선적으로 청소
3. 청소한 영역의 라이브 객체를 다른 영역으로 옮기고, 나머지는 비움

리전은 Eden, Survivor, old 영역으로 역할이 부여됨
Humonogous Object : 크기가 커서 여러 영역을 차이하는 객체를 위한 영역
GC 작업은 병렬로 진행되며, 각각 쓰레드가 자신만의 영역을 잡고 작업하여 충돌을 방지한다.

young-only : old 객체를 새로운 공간으로 이동
space-reclamation : 공간 회수

G1 Collector의 GC는 4단계, 세부적으로 6단계이다.
old gen의 점유율이 threshold를 넘어서면 young-only페이즈로 전환된다.
concurrent Start : 도달할 수 있는 객체들을 마킹한다.
Remark : 마킹을 끝

### Young GC
Live Object는 Age에 맞게 Survivor 영역, Old 영역으로 복사되며 기존 공간은 해지된다.
이후에 새로운 객체가 할당되는 Young 영역은 Survivor Region과 그 근처에 비어있는 Region이 된다.

### Concurrent Mark phase (Mark -> Remark) : Old GC 시작
Marking : 단일 쓰레드, 전체적으로 Concurrent하며, 이전 단계에서 변경된 정보를 바탕으로 빠르게 Initial Mark
Remarking : Suspend한 상태에서 멀티 쓰레드가 동시작업한다. 각 영역하다 도달 가능한 객체의 개수를 계산하고
Garbage 영역은 바로 해지된다.

### Old 영역 reclaim (Remark->evacuation(비우기))
Remarking : 멀티 쓰레드로 GC를 위해 도달 가능 객체의 비율이 낮은 영역을 골라낸다.
Evacuation : Young 영역의 GC를 포함하며, 앞의 Remark 단계에서 골라낸 Old 영역을 Young 영역과
같은 방법으로 정리한다. 이 과정을 거치면, 도달 가능 객체의 비율이 높은 영역들만 남게된다.

### Compaction
다른 GC와 다르게 동시적으로 압축한다. Region 단위로 작업을 수행하므로 가능한 일이다. Compaction의 주 목적은 Free Space를 병합하여
단편화를 방지하는 것이다.
Mark 단계에서 최소한의 Suspend 시간으로 Live Object를 골라내는 것 Parallel Compacion Collect와 비슷하다.

