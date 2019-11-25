package MainPackage;
import javax.swing.text.DateFormatter;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.SimpleFormatter;

public class Main {

    public static void main(String[] args) throws Exception {
        // a)
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        Date firstDate = sdf.parse("1-09-1939");
        Date secondDate = sdf.parse("8-05-1945");

        long diffrence = secondDate.getTime() - firstDate.getTime() + 1;
        System.out.println("a) "+TimeUnit.DAYS.convert(diffrence,TimeUnit.MILLISECONDS));

        // b)
        LocalDate localDate = LocalDate.of(2016,1,1);
        System.out.println("b) "+localDate.plusDays(67));

        //  c)
        LocalTime localTime = LocalTime.of(11,45);
        int sum;
        int amountOfAppears = 0;
        while(!localTime.equals(LocalTime.of(22,30))){
            int hour = localTime.getHour();
            int minute = localTime.getMinute();
            sum = getSumOfDigits(hour);
            sum += getSumOfDigits(minute);
            if(sum == 15) {
                //System.out.println(localTime);
                amountOfAppears++;
            }
            localTime = localTime.plusMinutes(1);
        }
        System.out.println("c) "+amountOfAppears);

        // d)
        LocalDate myBirth = LocalDate.of(1998,1,12);
        LocalDate testDate1 = LocalDate.of(2020,3,3);
        System.out.println("d) "+AmountOfLeapYears(myBirth,testDate1));

    }

    public static int AmountOfLeapYears(LocalDate birthDay, LocalDate dateNow){
        if(birthDay.isAfter(dateNow))
            return 0;
        int counter = 0;
        int year = birthDay.getYear();
        LocalDate firstYearDate = LocalDate.of(year,1,1);
        if(firstYearDate.isLeapYear()) {
            firstYearDate = LocalDate.of(year, 2, 29);
            if(birthDay.equals(firstYearDate) || birthDay.isBefore(firstYearDate))
                counter++;
        }
        LocalDate date = firstYearDate;
        while(true){
            date = date.plusYears(1);
            if(date.getYear() == dateNow.getYear()){
                if(dateNow.isLeapYear()) {
                    date = LocalDate.of(date.getYear(), 2, 29);
                    if (date.isEqual(dateNow) || date.isBefore(dateNow))
                        counter++;
                    return counter;
                }
                else
                    return counter;
            }
            if(date.isLeapYear())
                counter++;
        }
    }

    private static int getSumOfDigits(int number){
        int sum = 0;
        while (number > 0){
            sum += number%10;
            number/=10;
        }
        return sum;
    }
}
