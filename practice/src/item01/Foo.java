package item01;

public class Foo {
    private String name;

    // 전통적인 public 생성자
    public Foo(String name) {
        this.name = name;
    }

    // static factory method
    public static Foo withName(String name) {
        return new Foo(name);
    }

    public static void main(String[] args) {
        // 전통적인 public 생성자
        Foo foo = new Foo("fafi");

        // static factory method: 이름을 가질 수 있다.
        Foo foo2 = Foo.withName("fafi");
    }
}
