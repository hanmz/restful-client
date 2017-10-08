package com.github.hanmz;

import jdk.internal.org.objectweb.asm.Type;
import org.junit.Test;

/**
 * *
 * Created by hanmz on 2017/9/10.
 */
public class TypeTest {
  @Test
  public void test() throws ClassNotFoundException {
    Class clazz = Class.forName("com.github.hanmz.TypeTest");
    System.out.println(clazz.getTypeName());
    System.out.println(Type.getReturnType(clazz.getTypeName()));
    System.out.println(clazz.getName());
  }
}
