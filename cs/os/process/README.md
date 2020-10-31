# Process
1. 실행중인 프로그램의 인스턴스(여러 개도 실행 가능)
2. 다이나믹하고 엑티브한 엔티티
3. 실행하고 스케쥴링하기 위한 기본적인 단위
4. 프로세스는 PID가 할당되어 운영체제에 의해 구분 됨
5. 다음과 같은 것들을 포함함
> CPU contexts(registers) 를 포함함   
> OS resources(memory, open files, etc.)   
> 다른 정보들(PID, state, owner, etc.)

## 메모리 측면의 프로세스
프로그램이 실행되면 운영체제는 프로세스를 위해 가상의 메모리 공간을 생성함   
1. 코드 영역(read only segment) : 시작주소 0
2. 데이터, bss 섹션(read / write segment) : 시작주소 n 
3. run-time heap(new에 의해 실행시간에 생성) -> 위로 증가 : 시작주소 m (0xffffffff > m > n)
4. user stack(runtime에 정적으로 생성) -> 아래로 주소가 감소: 시작주소 0xffffffff    
stack pointer가 끝을 가리키고 있음 시작주소

PC : 코드가 어디까지 사용되었는지 나타내기 위해서 사용(쓰레드 별로 부여)   
SP : 스택이 어디까지 사용되었는지 나타내기 위해서 사용(쓰레드 별로 부여)   

![process memory](https://upload.wikimedia.org/wikipedia/commons/3/32/Virtual_address_space_and_physical_address_space_relationship.svg)

## 생성과정
1. 프로세스는 부모-자식 관계(계층구조)를 가지고 있음
2. 부모 프로세스를 fork(유일한 방법) 하여 자식 프로레스가 생성
3. 부모 프로세스의 전체 Address space를 복제해서 새롭게 자식을 위해 생성
4. 자식 프로세스 용 필요한 내부 데이터 세팅
5. 부모프로세스는 자식프로세스의 PID를 받고 자식 프로세느는 0을 받음 

## 프로세스 종료
1. Normal exit
2. Error exit
3. Fatal error
- resources 할당 초과
- 세그멘테이션 폴트
4. 다른 프로세스에 의한 종료(kill)

## state
1. new : 생성 
2. ready : 준비(프로세스가 동작할 준비가 됨)
3. running : 실제로 하드웨어 위에서 cpu를 사용하며 동작하는 단계
cpu를 일정시간 사용하면 interupt를 받아 ready상태로 변경하여 다시 대기
4. waiting : I/O가 필요하면 I/O 이벤트가 종료될때까지 waiting 상태 유지하다가 다 읽으면 ready상태 전환
5. terminate : 모든 명령을 종료하면 종료   
ready running waiting 상태를 계속 이동하며 프로세스가 동작함

## PCB(Process Control Block) 
1. 프로세스를 관리하는 모든 정보
> Process state    
> Program counter   
> CPU registers   
> CPU 스케쥴링 정보   
> 메모리 관리 정보(text, data, stack segment)   
> 소유자, IO, etc   

2. task_struct (Linux에서 사용하는 PCB 구조체)
> 1.4KB : linux에서는 이 정도 크기로 관리

3. 하드웨어 state 정보   
PC, SP(Stack pointer), register   
프로세스가 멈추면 PCB에 모두 저장   
나중에 다시 사용할 떄 PCB를 통해 복원

## Context switch(process switch)
1. 프로세스에서 다른프로세스로 CPU를 스위치하는 일
2. 오버헤드 발생
> PCB 내용 교체(저장 및 로딩)   
> 메모리 캐시, map flush하고 reloading
3. 하드웨어가 보조하면 조금 더 빠르게 동작함

## Process State Queues
Ready queue, Wait queue : 각 PCB는 자신의 상태에 따라 큐를 이동
각 PCB는 큐의 형태로 일렬로 연결되어 있음

## 프로세스간 커뮤니케이션
서로의 가상 메모리를 확인할 수 는 없음. 따라서 데이터 교환을 위해 다른 방법을 사용해야함
pipe, Sockets, shared memory