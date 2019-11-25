package MainPackage;

import org.junit.Test;

import static MainPackage.Main.AmountOfLeapYears;
import static org.junit.Assert.*;

import java.time.LocalDate;

public class Tests {

    @Test
    public void test1(){
        LocalDate date1 = LocalDate.of(1998,1,12);
        LocalDate date2 = LocalDate.now();
        assertEquals(5,Main.AmountOfLeapYears(date1,date2));
    }

    @Test
    public void test2(){
        LocalDate date1 = LocalDate.of(1996,3,12);
        LocalDate date2 = LocalDate.now();
        assertEquals(5,Main.AmountOfLeapYears(date1,date2));
    }

    @Test
    public void test3(){
        LocalDate date1 = LocalDate.of(1996,1,12);
        LocalDate date2 = LocalDate.now();
        assertEquals(6,Main.AmountOfLeapYears(date1,date2));
    }

    @Test
    public void test4(){
        LocalDate date1 = LocalDate.of(1996,1,12);
        LocalDate date2 = LocalDate.of(2020,3,3);
        assertEquals(7,Main.AmountOfLeapYears(date1,date2));
    }

    @Test
    public void test5(){
        LocalDate date1 = LocalDate.of(1996,3,12);
        LocalDate date2 = LocalDate.of(2020,1,3);
        assertEquals(5,Main.AmountOfLeapYears(date1,date2));
    }


}
