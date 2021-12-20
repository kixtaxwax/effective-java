# Item 03. private 생성자나 열거 타입으로 싱글턴임을 보증하라

싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다. 무상태 객체나 설계상 유일해야 하는 시스템 컴포넌트를 주로 싱글턴 클래스로 만든다. 그런데 클래스를 싱글턴으로 만들면 이를 사용하는 클라이언트를
테스트하기가 어려워질 수 있다. 타입을 인터페이스로 정의한 다음 그 인터페이스를 구현해서 만든 싱글턴이 아니라면 싱글턴 인스턴스를 mock으로 대체할 수 없기 때문이다.

## 싱글턴을 만드는 방식

- 생성자는 private으로 감춘다.
- 유일한 인스턴스에 접근할 수 있는 수단으로 public static 멤버를 마련해둔다.

## public static 멤버가 final 필드인 방식

```java
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();

    private Elvis() {}
}
```

- private 생성자가 멤버 인스턴스를 초기화할 때 딱 한 번 호출된다.
- public이나 protected 생성자가 없으므로 클래스가 초기화될 때 만들어진 인스턴스가 전체 시스템에서 하나뿐임이 보장된다.
- 다만, 리플렉션 API의 AccessibleObject.setAccessible을 사용해 private 생성자를 호출할 수 있으므로 이런 공격을 방어하려면 생성자를 수정하여 두 번째 객체가 생성되려 할 때 예외를
  던지게 하면 된다.

## public static 팩터리 메서드인 방식

```java
public class Elvis {
    private static final Elvis INSTANCE = new Elvis();

    private Elvis() {}

    public static Elvis getInstance() { return INSTANCE; }
}
```

### 첫 번째 방식의 장점

- 해당 클래스가 싱글턴임이 API에 명백히 드러난다.
- 간결하다.

### 두 번째 방식의 장점

- API를 바꾸지 않고도 싱글턴이 아니게 변경할 수 있다. 유일한 인스턴스가 아니라 각각 다른 인스턴스를 생성해서 넘겨주게 할 수 있다.
- 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다.
- 정적 팩터리의 메서드 참조를 supplier로 사용할 수 있다. ex) Elvis::getInstance를 Supplier<Elvis>로 사용하는 식이다.

두 번째 방식의 장점들이 굳이 필요하지 않다면 public 필드 방식이 좋다.

싱글턴 클래스를 직렬화하려면 단순히 Serializable을 구현한다고 선언하는 것만으로는 부족하다. 모든 인스턴스 필드를 transient로 선언하고 readResolve 메서드를 제공해야 한다. 이렇게 하지
않으면 직렬화된 인스턴스를 역직렬화할 때마다 새로운 인스턴스가 만들어진다. 가짜 객체가 탄생할 수 있다는 뜻이다.

readResolve 메서드는 싱글턴임을 보장해준다.

## 세 번째 방식: 원소가 하나인 Enum 타입

public 필드 방식보다 간결하고, 추가적인 노력 없이 직렬화할 수 있고, 아주 복잡한 직렬화 상황이나 리플렉션 공격에서도 제2의 인스턴스가 생기는 일을 완벽히 막아준다. 대부분 상황에서는 이 방식이 가장 좋은
방법이다.

단, 만들려는 싱글턴이 Enum 외의 클래스를 상속해야 한다면 이 방법은 사용할 수 없다.
