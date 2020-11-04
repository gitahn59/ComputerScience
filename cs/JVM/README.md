# JVM

![JVM architecture](https://upload.wikimedia.org/wikipedia/commons/d/dd/JvmSpec7.png)

## 자바 클래스로더
자바 클래스를 JVM에 동적으로 로딩하는 JRE(자바 런타임 환경)의 일부이다.    

## 자바 바이트 코드
자바 가상머신이 실행하는 명령어, 각 바이트 코드는 1바이트로 구성되지만 파라미터가 사용되는 경우
몇 바이트로 구성될 수 있음.

## 메모리
메모리는 크게 모든 쓰레드가 공유하는 영역, 각자 소유하는 영역으로 구분된다.  

### 개별 소유 영역
Thread 각각이 소유하는 영역으로 생성 삭제에 따라 생성되고 삭제되는 영역
#### 레지스터 
레지스터는 데이터를 신속하게 저장, 사용하는 공각으로 data, address, status 등으로 구성된다.
가상 머신인 JVM에도 PC, optop, frame, vars 등 Stack 영역을 관리하는 3개의 레지스터를 사용한다.
각 레지스터의 크기는 32bit(= JVM 1 word) 이다.
PC 레지슽어는 현재 메서드가 실행할 다음 bytecode의 adderss를 가진다.
이 때 Native 메서드를 처리할 때는 PC레지스터는 undefined 값을 가진다.
각 레지스터는 다음 영역의 포인터를 가진다.
optop Register -> Operand Stack
vars Register -> Local Variable
frame Register -> Excution Enviornment

#### JVM 스택 
Stack은 Frame을 여러 장 쌓은 구조이며, 1개의 Frame은 정확히 1개의 메서드에 대응한다.
Frame은 Operand Stack, Local Variable, Excution Enviornment로 구성된다.

operand stack은 연산 과정중 피연산자가 들어오고 나가는 공간이다.
```
int i1 =1;
int i2 = 2;
int i3 = i1 + i2;
```
- int 1을 Operand Stack에 push
- Operand Stack의 int(1)를 Local Variable slot 1에 pop

- int 2을 Operand Stack에 push
- Operand Stack의 int(2)를 Local Variable slot 2에 pop

- Local Variable slot 1의 int(1)를 Operand Stack 에 push
- Local Variable slot 1의 int(2)를 Operand Stack 에 push
- Operand Stackdml 두 int(1,2)를 add
- Operand Stack의 int(3)를 Local Variable slot 3에 pop

Operand Stack의 각 slot 크기는 32bit(=1 word)로 long, double은 2개의 slot을 사용한다.
Local Variable 영역에는 메서드의 parameter와 local variable이 존재한다. 따라서 32bit 단위의
스롯을 사용한다. primitive type 대부분은 1개의 slot을 사용하지만, long 과 double은 64bit이므로
연속된 2개의 slot을 사용한다.

Objsect, String 등 크기를 알 수 업는 객체는 Local Variable 배열이 아니라 Heap 영역에 저장되며
그 참조만 배열에 저장된다. 이 참조는 32bit 주소로 1개의 slot을 사용한다.

Execution Environment 는 Dynamic Linking과 리턴값, Exception 정보를 담는다.

JVM의 스택 처리 중에는 각 Thread가 사용 가능한 Stack의 크기가 고정되어있어서,
Thraea가 최대 Stack 크기를 넘어서면 발생하는 StackOverflowError과
메모리가 충분하지 않으면 스택을 생성하지 못하는 OutOfMemoryError가 발생한다.

#### 네이티브 메소드 스택
C언어 등 타 언어 코드를 실행할 때 사용되며, JVM 스택이 아닌 Native 메서드 스택 영역에서 메서드를 처리한다.

### 전체 공유 영역
#### 힙 : 객체(인스턴스) 정보 저장, heap 상세 설명 참고
#### 메소드 : 클래스 정보 저장, 클래스 이름, 메소드, 변수, static 변수, 일반 변수 등
Hotspot 계열 JVM의 Permanent Generation 영역을 말한다. Classloader Reference, 런타임 Constant Pool,
Field 정보, Method 정보, Method 코드 등을 저장한다.
Runtime Constant Pool : 클래스나 인터페이스가 사용하는 리터럴 상수와 Type, 메서드, 필드에 대한 참조 정보
이 정보를 통해 각 객체의 주소를 찾는다.
JAVA 8 부터는 Permanent Generation이 없어지고 Native 메모리 내의 Metaspace 영역으로 대체되었다.
따라서 클래스 메타 데이터에 사용할 수 있는 메모리 양은 무제한이다. 이 영역은 OS가 자동으로 크기를 조절한다.
또한 기존 Static Object는 Heap 영역으로 이동되어 GC의 대상으로 추가되었다.

Heap 영역은 JVM에 의해 관리되며, Native 메모리는 OS 레벨에서 관리한다.

## 실행 엔진
- 인터프리터 : 바이트 코드를 라인별로 읽어 네이티브 코드로 변환
- JIT(Just In Time) 컴파일러
> 바이트 코드에서 반복되는 부분을 찾아 JIT 컴파일러가 미리 네이티브 코드로 변환   
> 반복되는 코드를 실행할때는 인터프리터를 거치지 않고 바로 네이티브 코드 사용   
> 인터프리터의 속도를 보완   

- GC(Garbage Collector)

## JVM 옵션
옵션은 크게 Standard Option과 Non-Standard Option으로 나누어 볼 수 있다.
- Standard Option : JVM의 공통적 옵션, '-'을 붙여 표기
- Non-Standard Option : JVM마다, 버전마다 약간씩 다르게 표기

설정과 성능 개선을 위해 Parameter 등의 목적으로 사용한다. 
1. 옵션 앞에 "-X"를 붙이면 Macro한 부분을 제어한다.
2. 옵션 앞에 "-XX:"를 붙이면 Micro한 측면을 제어한다.
-X옵션을 위해 -XX를 부가적으로 더 설정하기도 한다.

XX 옵션은 형식에따라 Boolean, Numeric, String으로 나뉘며 표현 방식에 차이가 있다.
1. Boolean : On으로 설정하는 경우 -XX:+<옵션> 형식으로 +를 붙인다.
Off로 설정하년 경우 -XX:-<옵션> 형식으로 -를 붙인다. 이를 통해 default로 설정된 옵션을 끌 수 있다.

2. Numerice : -XX:<옵션>=<numerice> 형식으로 k,K ,m,M, g,G로 Byte 단위로 인식한다.

3. String : -XX:<옵션>=<string> : 형식으로 특정 파일 또는 Path의 지정에 사용


