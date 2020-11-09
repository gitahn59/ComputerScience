# 직렬화(Serialization)

객체를 컴퓨터에 저장했다가 다음에 다시 꺼내 쓰는 기법, 이를 통해 네트워크를 통해 객체를 주고받을 수 있다.
직렬화란 객체를 데이터 스트림으로 만든다. 객체는 스트림을 통해 쓰여지고, 연속적인 데이터로 변환된다.   
역직렬화를 그 반대과정을 의미한다.

객체는 클래스에 정의된 인스턴스 변수의 집합이다. 객체에는 클래스 변수(static)나, 메서드가 포함되지 않는다.    
인스턴스 변수는 다른 값을 가질 수 있으므로 모두 저장해야하고, 메서드는 변화하지 않으므로 저장할 필요가 없다.  
따라서 객체를 저장한다는 의미는 인스턴스 변수의 값을 저장하는 의미와 동치이다.    
인스턴스 변수가 전부 기본형이면 값을 저장하는 일이 간단하다. 하지만 타입이 참조형인 경우를 고려해야한다.     
자바에서는 ObjectInputStream과 ObjectOutputStream의 사용법을 알면 손쉽게 직렬화를 수행할 수 있다.     

## ObjectInputStream과 ObjectOutputStream

두 스트림은 각각 InputStream과 OutputStream을 직접 상속하지만, 기반 스트림이 필요한 보조 스트림이다.    
따라서 객체를 생성할 때 스트림을 지정해야 한다.

```java
ObjectInputStream(InputStream in)
ObjectOutputStream(OutputStream out)
```

```java
// 객체를 파일에 직렬화
ObjectOutputStream out = new ObjectOutputStream(fos);
out.writeObject(new myObject());

ObjectInputStream in = new ObjectInputStream(fis);
MyObject myObject = (MyObject)in.readObject(); // 명시적 형변환 필요
```

Stream은 readObject()와 writeObject()이외에도 여러 메서드를 제공하며,    
각 메서드들은 직렬화와 역직렬화를 직접 구현할 때 주로 사용한다.

## 직렬화 인터페이스 : Serializable

직렬화가 가능한 클래스를 만들려면 Serializable 인터페이스를 구현한다.    
만약 클래스가 Serializable을 구현한 super class를 상속받는 경우라면 다시 구현할 필요는 없다.     
하지만, 조상클래스가 Serializable를 구현하지 않은 경우, 자식 클래스의 인스턴스 변수만 직렬화된다.   
따라서 조상클래스의 변수도 대상에 포함하려면, 직접 직렬화되는 코드를 추가해야 한다.

인스턴스 변수중에 직렬화 불가능한 변수가 있으면 NotSerializableException이 발생한다.   
하지만 그 인스턴스의 참조로 결정되는 것이 아닌 실제 객체를 기준으로 직렬화된다.

명시적으로 직렬화대상에서 제외하려면 키워드 transient를 붙여준다.    
패스워드나, 필요없는 값을 직렬화에서 제외하면 각각 기본값으로 설정된다.(기본형, null)     

직접 구현한 직렬화를 사용하려면 defaultReadObject, defaultWriteObjet 메서드를 사용한다.
default 메서드는 클래스에 직접 구현한 writeObject, readObject를 수행하여 사용자 정의 직렬화를 수행한다.
```java
private void writeObject(ObjectOutputStream out) thorows IOException{
    // write 메서드를 사용하여 직렬화 수행
    out.defaultWriteObject();
    out.writeUTF(str1);
    out.writeUTF(str2);
}

private void readObject(ObjectInputStream in) thorows IOException{
    // read 메서드를 사용하여 직렬화 수행
    in.defaultIReadObject();    
    str1 = in.readUTF();
    str2 = in.readUTF();
}
```
private로 지정하여 서브 클래스의 상속을 못하게하여, 서브 클래스에서는 고유의 직렬화 함수를 작성하도록 한다.
두 메서드는 직렬화 과정에서 리플렉션을 통해 수행되므로 private는 문제없다.   
반드시 default 메서드를 가장 먼저 호출해야 한다.

## 직렬화 가능 클래스의 버전관리
때로는 버전을 수동으로 관리하여 직렬화와 역직렬화가 동일한 대상을 가리키도록 해야한다.    
클래스의 버전을 수동으로 관리하려면 serialVersionUID를 추가로 정의해야 한다.      

```java
static final long serialVersionUID = (num) L;

```
