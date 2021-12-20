package item03;

public class FactoryElvis {
    private static final FactoryElvis INSTANCE = new FactoryElvis();

    private FactoryElvis() { }

    public static FactoryElvis getInstance() { return INSTANCE; }

    public void leaveTheBuilding() {
        System.out.println("I'm outta here!");
    }
}
