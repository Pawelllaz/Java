package MainPackage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int[] tab = {4,10,3,7,4,1,6,2};
        Class cls = Class.forName("MainPackage.MaxSearchAlgorithms");
        //ArrayList<Method> list = new ArrayList<>();

        MaxSearchAlgorithms maxSearchAlgorithms = new MaxSearchAlgorithms();
        for(Method method: cls.getDeclaredMethods()) {
            //System.out.println(method.getName());
            if(method.getName().contains("Scan"))
               System.out.println(method.invoke(maxSearchAlgorithms,tab));
        }

    }
}
