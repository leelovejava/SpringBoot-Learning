package com.leelovejava.interview;

/**
 * 1.Java中的“==”和equals()方法有什么区别？
 */
public class Test16 {
    public static void main(String[] args) {
        int i = 10;
        int j = 10;
        System.out.println(i == j);

        Person p1 = new Person("韩梅梅");
        Person p2 = new Person("韩梅梅");
        System.out.println(p1 == p2);//false
        System.out.println(p1.equals(p2));//false

        String str1 = new String("abc");
        String str2 = new String("abc");
        System.out.println(str1 == str2);//false
        System.out.println(str1.equals(str2));//true


        System.out.println(p1.name == p2.name);//true
    }
}

class Person {
    String name;

    public Person(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
