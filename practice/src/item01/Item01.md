# Item 01. 생성자 대신 정적 팩토리 메서드를 고려하라

클래스의 인스턴스(객체)를 얻는 전통적인 수단으로 아래와 같이 public 생성자가 있다.

```java
Foo foo = new Foo();
```

생성자와는 별도로, 아래와 같이 정적 팩터리 메서드(static factory method)를 통해 인스턴스를 얻을 수 있다.

```java
public static Boolean valueOf(boolean b) {
    return (b ? Boolean.TRUE : Boolean.FALSE);
}
```

정적 팩터리 메서드가 생성자보다 좋은 장점 다섯 가지가 있다.

## 장점 1: 이름을 가질 수 있다. (어떤 객체를 생성하도록 할 것인지 메서드 이름을 통해 명시적으로 표현할 수 있다.)

생성자에 넘기는 매개변수와 생성자 자체만으로는 반환될 객체를 잘 설명하지 못한다. 반면, 정적 팩터리 메서드는 이름만 잘 지으면 반환될 객체의 특성을 쉽게 묘사할 수 있다. 

그 예로 소수를 반환하는 `BigInteger.probablePrime`을 들고 있다. 

값이 소수인 `BigInteger` 인스턴스를 반환한다는 의미를 무엇이 더 잘 설명하는지 생각해보자.

```java
// public 생성자
BigInteger(int,int,Random)

// 정적 팩터리 메서드
BigInteger.probablePrime
```

또, 생성자는 시그니처에 제약이 있다. 하나의 시그니처로는 하나의 생성자만 만들 수 있다. 

하나의 클래스에 시그니처가 같은 생성자가 여러 개 필요할 것 같으면, 생성자를 정적 팩터리 메서드로 바꾸고 각각의 차이를 잘 드러내는 이름을 지어주자.

## 장점 2: 반드시 새로운 객체를 만들 필요는 없다.

불변 클래스는 미리 만들어둔 인스턴스 또는 새로 생성한 인스턴스를 캐싱하여 재활용하는 방식으로 불필요한 객체 생성을 피할 수 있다. `Boolean.valueOf(boolean)` 메서드는 객체를 아예 생성하지 않는다.

```java
public final class Boolean implements java.io.Serializable, Comparable<Boolean> {
    
    // 인스턴스를 미리 만들어둔다.
    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    // ...
    
    // 미리 만들어둔 인스턴스를 반환한다.
    public static Boolean valueOf(boolean b){
        return (b ? TRUE : FALSE);
    }
    
    // ...
}
```

정적 팩터리 메서드로 `new` 키워드를 통한 불필요한 객체 생성을 피할 수 있기 때문에, 동일한 객체가 자주 요청되는 상황이라면 성능을 상당히 끌어올려 준다.

## 장점 3: 반환 타입의 하위 타입 인스턴스를 반환할 수도 있다.

클래스에서 만들어 줄 객체의 클래스를 선택하는 유연함이 있다. 반환 타입의 하위 타입의 인스턴스를 만들어줘도 되니까, 반환 타입은 인터페이스로 지정함으로써 그 인터페이스의 구현체는 `API`로 노출 시키지 않으면서도 그 구현체의 인스턴스를 만들어 줄 수 있다는 말이다. `java.util.Collections`가 그 예에 해당한다.

`java.util.Collections`는 인터페이스의 구현체로써 45종류에 달하는 인스턴스를 제공하지만 그 구현체들은 전부 `non-public`이다. 즉 인터페이스 뒤에 감춰져 있음으로써 `public`으로 제공해야 할
`API`를 줄였을 뿐 아니라 개념적인 무게(conceptual weight)까지 줄일 수 있었다.

여기서 개념적인 무게란, 프로그래머가 어떤 인터페이스가 제공하는 `API`를 사용할 때 알아야 할 개념의 개수와 난이도를 말한다.

자바 8부터 인터페이스에 public static 메서드를 추가할 수 있게 되었지만 private static 메서드는 자바 9부터 추가할 수 있다. 따라서 자바 8부터 인터페이스에 public static 메서드를
사용해서 그 인터페이스의 구현체를 메서드를 제공할 수도 있지만 private static 메서드가 필요한 경우, 자바 9가 아니면 기존처럼 별도의 (인스턴스를 만들 수 없는, java.util.Collections
같은) 클래스를 사용해야 할 수도 있다.

## 장점 4: 리턴하는 객체의 클래스가 입력 매개변수에 따라 매번 다를 수 있다.

장점 3과 같은 이유로 객체의 타입은 다를 수 있다. `EnumSet` 클래스 (아이템 36)는 생성자 없이 public static 메서드, allOf(), of() 등을 제공한다. 그 안에서 리턴하는 객체의 타입은
enum 타입의 개수에 따라 `RegularEnumSet` 또는 `JumboEnumSet`으로 달라진다.

그런 객체 타입은 노출하지 않고 감춰져 있기 때문에 추후에 `JDK`의 변화에 따라 새로운 타입을 만들거나 기존 타입을 없애도 문제가 되지 않는다.

## 장점 5: 리턴하는 객체의 클래스가 public static 팩토리 메서드를 작성할 시점에 반드시 존재하지 않아도 된다.

장점 3, 4와 비슷한 개념이다. 이러한 유연성을 제공하는 static 팩토리 메서드는 서비스 프로바이더 프레임워크의 근간이다. `JDBC`를 예로 들고 있다.

- 서비스 인터페이스 : 구현체의 동작을 정의한다.
- 제공자 등록 API : 제공자가 구현체를 등록할 때 사용하는 API이다.
- 서비스 접근 API : 클라이언트가 서비스의 인스턴스를 얻을 때 사용하는 API이다.

클라이언트가 서비스 접근 API를 통해 원하는 구현체의 조건을 명시하면 그에 맞는 구현체를 반환한다. 구현체 제공을 프레임워크가 통제함으로써 구현체로부터 클라이언트를 분리한다.

서비스 프로바이더 프레임워크는 서비스의 구현체를 대표하는 서비스 인터페이스와 구현체를 등록하는데 사용하는 `제공자 등록 API`, 그리고 클라이언트가 해당 서비스의 인스턴스를 가져갈 때 사용하는 `서비스 액세스 API`가
필수로 필요하다. 부가적으로, 서비스 인터페이스의 인스턴스를 제공하는 `서비스 프로바이더 인터페이스`를 만들 수도 있는데, 그게 없는 경우에는 리플렉션을 사용해서 구현체를 만들어 준다.

`JDBC`의 경우, `DriverManager.registerDriver()`가 프로바이더 등록 API. `DriverManager.getConnection()`이 서비스 액세스 API. 그리고 `Driver`가 서비스
프로바이더 인터페이스 역할을 한다.

자바 6부터는 `java.util.ServiceLoader`라는 일반적인 용도의 서비스 프로바이더를 제공하지만, `JDBC`가 그보다 이전에 만들어졌기 때문에 `JDBC`는 `ServiceLoader`를 사용하진 않는다.

## 단점 1: public 또는 protected 생성자 없이 static public 메서드만 제공하는 클래스는 상속할 수 없다.

정적 팩토리 메서드만 제공하면 하위 클래스를 생성할 수 없다. 상속을 위해서는 public 혹은 protected 생성자가 필요하다.

따라서, Collections 프레임워크에서 제공하는 편의성 구현체(java.util.Collections)는 상속할 수 없다. 오히려 불변 타입(아이템 17)인 경우나 상속 대신 컴포지션을 권장(아이템 18)하기
때문에 장점이라 받아들일 수도 있다.

## 단점 2: 프로그래머가 static 팩토리 메서드를 찾는게 어렵다.

생성자는 Javadoc 상단에 모아서 보여주지만 static 팩토리 메서드는 API 문서에서 특별히 다뤄주지 않는다. 따라서 클래스나 인터페이스 문서 상단에 팩토리 메서드에 대한 문서를 제공하는 것이 좋다.

## 참고

- [ServiceLoader](https://docs.oracle.com/javase/9/docs/api/java/util/ServiceLoader.html)

