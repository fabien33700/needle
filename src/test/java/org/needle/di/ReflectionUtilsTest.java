package org.needle.di;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.needle.di.mocks.AnnoA;
import org.needle.di.mocks.AnnoB;
import org.needle.di.mocks.Dummy;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

public class ReflectionUtilsTest {

    @BeforeAll
    static void setup() {

    }

    /**
     * Passing
     * @see ReflectionUtils#getMemberNameFromSetter(String)
     */
    @Test
    void test_memberNameFromSetter_ok() {
        assertThat(ReflectionUtils.getMemberNameFromSetter("setFirstName"),
                equalTo("firstName"));

        assertThat(ReflectionUtils.getMemberNameFromSetter("setNumberOfChild"),
                equalTo("numberOfChild"));
    }

    /**
     * Not passing (not a setter name)
     * @see ReflectionUtils#getMemberNameFromSetter(String)
     */
    @Test
    void test_memberNameFromSetter_ko_notSetterName() {
        assertThat(ReflectionUtils.getMemberNameFromSetter("getLastName"),
                isEmptyString());

        assertThat(ReflectionUtils.getMemberNameFromSetter("lastName"),
                isEmptyString());

        assertThat(ReflectionUtils.getMemberNameFromSetter(null),
                isEmptyString());
    }

    /**
     * Not passing (method name case mismatch)
     * @see ReflectionUtils#getMemberNameFromSetter(String)
     */
    @Test
    void test_memberNameFromSetter_ko_disrespectingCase() {
        assertThat(ReflectionUtils.getMemberNameFromSetter("setNumberOfChild"),
                not(equalTo("numberofchild")));
    }

    /**
     * Passing (setter ok)
     * @see ReflectionUtils#isSetter(Class, Method)
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_isSetter_ok() throws Exception {
        assertThat(
                ReflectionUtils.isSetter(
                        Dummy.class,
                        Dummy.class.getDeclaredMethod("setFirstName", String.class)),
                is(true));
    }

    /**
     * Not passing (not a setter method name)
     * @see ReflectionUtils#isSetter(Class, Method)
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_isSetter_ko_badMethodName() throws Exception {
        assertThat(
                ReflectionUtils.isSetter(
                        Dummy.class,
                        Dummy.class.getDeclaredMethod("getFirstName")),
                is(false));
    }

    /**
     * Not passing (wrong return type : must be Void)
     * @see ReflectionUtils#isSetter(Class, Method)
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_isSetter_ko_wrongReturnType() throws Exception {
        assertThat(
                ReflectionUtils.isSetter(
                        Dummy.class,
                        Dummy.class.getDeclaredMethod("setAge", int.class)),
                is(false));
    }

    /**
     * Not passing (wrong parameters count : > 1)
     * @see ReflectionUtils#isSetter(Class, Method)
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_isSetter_ko_wrongParametersCount() throws Exception {
        assertThat(
                ReflectionUtils.isSetter(
                        Dummy.class,
                        Dummy.class.getDeclaredMethod("setAge", int.class, Object.class)),
                is(false));
    }

    /**
     * Not passing (types mismatch between setter and matching field)
     * @see ReflectionUtils#isSetter(Class, Method)
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_isSetter_ko_mismatchParameterType() throws Exception {
        assertThat(
                ReflectionUtils.isSetter(
                        Dummy.class,
                        Dummy.class.getDeclaredMethod("setSize", double.class)),
                is(false));
    }

    /**
     * Not passing (no matching field found for the setter)
     * @see ReflectionUtils#isSetter(Class, Method)
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_isSetter_ko_noMatchingField() throws Exception {
        assertThat(
                ReflectionUtils.isSetter(
                        Dummy.class,
                        Dummy.class.getDeclaredMethod("setNothing", Object.class)),
                is(false));
    }

    /**
     * Passing (on constructor)
     * @see ReflectionUtils#hasOneAnnotation(AnnotatedElement, Class[])
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_hasOneAnnotation_ok_constructor() throws Exception {
        assertThat(
                ReflectionUtils.hasOneAnnotation(
                        Dummy.class.getDeclaredConstructor(), AnnoA.class),
                is(true));
    }

    /**
     * Passing (on field)
     * @see ReflectionUtils#hasOneAnnotation(AnnotatedElement, Class[])
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_hasOneAnnotation_ok_field() throws Exception {
        assertThat(
                ReflectionUtils.hasOneAnnotation(
                        Dummy.class.getDeclaredField("firstName"), AnnoA.class),
                is(true));
    }

    /**
     * Passing (on method)
     * @see ReflectionUtils#hasOneAnnotation(AnnotatedElement, Class[])
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_hasOneAnnotation_ok_method() throws Exception {
        assertThat(
                ReflectionUtils.hasOneAnnotation(
                        Dummy.class.getDeclaredMethod("setFirstName", String.class), AnnoB.class),
                is(true));
    }

    /**
     * Not passing (none of annotation)
     * @see ReflectionUtils#hasOneAnnotation(AnnotatedElement, Class[])
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_hasOneAnnotation_ko_noAnnotations() throws Exception {
        assertThat(
                ReflectionUtils.hasOneAnnotation(
                        Dummy.class.getDeclaredMethod("hello"), AnnoA.class, AnnoB.class),
                is(false));
    }

    /**
     * Not passing (none of annotation)
     * @see ReflectionUtils#hasOneAnnotation(AnnotatedElement, Class[])
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_hasOneAnnotation_ko_noneAnnotationsFound() throws Exception {
        assertThat(
                ReflectionUtils.hasOneAnnotation(
                        Dummy.class.getDeclaredConstructor(), AnnoB.class),
                is(false));
    }

    /**
     * Passing
     * @see ReflectionUtils#describeMethod(Method)
     * @throws Exception should not be raised : method name are
     *    hardcoded both in test specs and mocks class
     */
    @Test
    void test_describeMethod_ok() throws Exception {
        assertThat(ReflectionUtils.describeMethod(
                Dummy.class.getDeclaredMethod("testDescribing",
                        String.class, float.class, double.class, byte.class,
                        int.class, long.class, Object.class, boolean.class)),
                is("testDescribing(String, float, double, byte, int, long, Object, boolean) : Object"));

        assertThat(ReflectionUtils.describeMethod(
                Dummy.class.getDeclaredMethod("setFirstName", String.class)),
                is("setFirstName(String) : void"));

    }

}
