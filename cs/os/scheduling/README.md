# CPU Scheduling
다음에 실행할 프로세스를 결정하는 방법(실행할 수 있는 프로레스들은 주어짐)   
이는 자주 발생하고, 빠르게 이루어져야 한다.

## 고려사항
- All systems   
Starvation(기아) x : 프로세스가 진행되는 것을 방해받는 상황, 동기화, 스케쥴링, 자원의 문제로 발생가능
공평함    
균형(자원들을 모두 잘 사용)   

- Batch systems   
Throughput : 시간당 처리량   
Turnaround time : 작업이 시작되고 완료되는데 걸리는 시간   
CPU utilization : CPU를 바쁘게 사용

- Interactive systems   
Response time : 반응 시간   
Proportionality : 유저의 기대치 만족

- Real-time systems   
Meeting deadline : 제한 시간의 만족   
Predictability(예측가능성) 

## 비선점 스케쥴링
Non-preemtive 스케쥴링은 스케쥴러가 다른 프로세스의 CPU 사용을 빼앗지 못한다.
즉 프로세스가 스스로 CPU를 yield 해야함

## 선점형 스케쥴링
스케쥴러가 인터럽트를 통해 CPU를 뺴앗아 올 수 있다.
다만, 프로세스가 중요한 작업을 하고 있거나, 시스템 콜을 수행하고 있거나 하는 경우를 고려해야 함.

## Process와 스케쥴링
1. CPU burst 프로세스 : 계산을 많이 하는 프로세스
2. I/O burst 프로세스 : 입출력이 많아 대기 시간이 많은 프로세스

## FCFS/FIFO(NP)
들어온 순서대로 처리하는 스케쥴링 방식   
문제점 : 실행시간이 짧은 프로세스가 긴 시간을 기다릴 수 있음.

## SJF(NP)
Shortest Job First : CPU를 가장 짧게 사용하는 프로세스 먼저 스케쥴링   
가장 짧은 작업부터 수행하면 평균 대기시간을 가장 짧게 만들 수 있음   
문제점 : 어떤 프로세스의 수행 시간을 알 수 없음. 기아 현상이 발생 할 수 있음

## SRTF(P)
Shortest Remaining Time First = SJF의 P 버전   
그 때 그 때 가장 짧게 CPU를 사용할(남아있는 시간이 적은) 프로세스로 교체하는 스케쥴링

## RR(P)
Round Robin 스케쥴링 : Ready Q가 원형으로 되어있음   
각 작업은 time slice만큼만 동작함(10~100ms)
- FIFO 방식이기 때문에 기아가 발생하지 않음
- SJF 보다는 turn around time이 나쁘지만, 응답 시간이 더 나음

## Priority(NP)
우선순위 스케쥴링, 우선순위에 따라 프로세스를 선택   
SJF는 우선순위가 CPU burst time으로 결정되는 Priority 스케쥴링이라고 볼 수 있음   
RR, FIFO는 같은 우선순위를 가진 Priority 스케쥴링이라고 볼 수 있음

1. 기아 문제 => aging
만약 높은 우선순위를 가지는 프로레스가 계속 생성되면, 낮은 우선순위를 가지는 프로세스가 계속 대기하게 됨   
따라서 대기시간에 따라 우선순위를 조정할 필요가 있음
- 대기시간이 길어지면 우선순위 증가
- CPU를 많이 사용하면 우선순위 감소

2. priority inversion 문제(우선순위 역전 문제)
높은 우선순위의 프로세스가 낮은 우선순위의 프로세스에 의해 동작하지 못하는 현상
낮은 프로세스가 lock을 가지고 CPU를 선점당했을때 높은 프로세스가 그 lock이 필요하면 동작할 수 없음   
PIP(Priroity inheritance protocol) : 우선순위를 잠시 빌려주어 락을 가진 낮은 프로세스가 빠르게 작업을 끝내도록 함
PCP(Priroity ceiling protocol) : 우선순위를 치대 우선순위로 할당하여 락을 가진 프로세스가 빠르게 작업을 끝내도록 함

Multilevel Feedback Queue(MLFQ)
1. job이 여러 큐를 왔다갔다하면서 작업을 처리하도록 함
2. 각 큐는 우선순위를 가지고 있음
대기시간, 작업 들을 고려하여 큐의 이동을 통해 에이징을 구현함

Q0 : RR로 돌면서 time quantum이 8m   
Q1 : RR로 돌면서 16m   
Q2 : FCFS   

작업을 마무리하지 못하면 Q0에서 Q1으로 이동, Q1에서 Q2로 이동   
아래 레벨로 이동할수록 우선순위가 aging 됨   

## UNIX 스캐쥴러
선점형, 우선순위 베이스(지정하지 않으면 기본값), RR(Time-shared 방식), MLFQ 사용

## 원칙
I/O bound task에게 높은 우선순위를 부여하여 조금 더 CPU를 먼저 선택되도록 함 : 보통 CPU를 짧게 사용함(SJF의 원리)
