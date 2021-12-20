package item03;

public class FieldElvis {
    public static final FieldElvis INSTANCE = new FieldElvis();

    private FieldElvis() { }

    public void leaveTheBuilding() {
        System.out.println("I'm outta here!");
    }
}
