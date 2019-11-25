package MainPackage;
//import org.junit.After;
//import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class Tests {

    @Test
    public void test1(){
        Levenshtein lev = new Levenshtein();
        assertEquals(0, Double.compare(1.5,lev.LevQWERTY("kot","kita")));
    }

    @Test
    public void test2(){
        Levenshtein lev = new Levenshtein();
        assertEquals(0, Double.compare(0.5,lev.LevQWERTY("kwiat","kwist")));
    }

    @Test
    public void test3(){
        Levenshtein lev = new Levenshtein();
        assertEquals(0, Double.compare(2,lev.LevQWERTY("drab","dal")));
    }

    @Test
    public void test4(){
        Levenshtein lev = new Levenshtein();
        assertEquals(0, Double.compare(1.5,lev.LevQWERTY("pawel","owel")));
    }

    @Test
    public void test5(){
        Levenshtein lev = new Levenshtein();
        assertEquals(0, Double.compare(2,lev.LevQWERTY("szczecin","szczecinek")));
    }

    @Test
    public void test6(){
        Levenshtein lev = new Levenshtein();
        assertEquals(0, Double.compare(4,lev.LevQWERTY("marka","ariada")));
    }

    @Test
    public void test7(){
        Levenshtein lev = new Levenshtein();
        assertEquals(0, Double.compare(3.5,lev.LevQWERTY("marka","arjada")));
    }
}
