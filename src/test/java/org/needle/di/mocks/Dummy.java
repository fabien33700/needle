package org.needle.di.mocks;

/**
 * Dummy class with mocks methods for testing ReflectionUtils
 * utiliy class' functions.
 * @author fabien33700 <fabien DOT lehouedec AT gmail DOT com>
 */
@SuppressWarnings("unused")
@AnnoA
public class Dummy {

    @AnnoA
    private String firstName;

    private int age;

    private float size;

    public String getFirstName() {
        return firstName;
    }

    @AnnoA
    public Dummy() {

    }

    @AnnoB
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int setAge(int age) {
        this.age = age;
        return age;
    }

    public void hello() {}

    public void setAge(int age, Object something) {}

    public void setSize(double size) {
        this.size = (float) size;
    }

    public void setNothing(Object something) {}

    public Object testDescribing(String s, float f, double d, byte bt,
                                 int i, long l, Object o, boolean b) {
        return null;
    }
}
