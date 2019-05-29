import javax.management.InstanceNotFoundException;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.TreeMap;

public class zadanie7
{
    public static void main(String[] args) 
    {
        Firm f1 = new Firm("dobrafirma", "zielona 1", new Phone(48, 111222333));
        Firm f2 = new Firm("zlafirma", "czerwona 2", new Phone(48, 444555666));
      	Firm f3 = new Firm("posredniafirma", "niebieska 3", new Phone(48, 777888999));       	 
       	Person p1 = new Person("Janusz", "Kowalski", "zolta 4", new Phone(48, 123456789));
        Person p2 = new Person("Pawel", "Lazicki", "biala 5", new Phone(48, 987654321));
        Person p3 = new Person("Krzysztof", "Nowak", "czarna 6", new Phone(48, 147852369));


        TreeMap<Phone, PhoneBook> container = new TreeMap<Phone, PhoneBook>();
        container.put(f1.number, f1);
        container.put(f2.number, f2);
        container.put(f3.number, f3);
        container.put(p1.number, p1);
        container.put(p2.number, p2);
        container.put(p3.number, p3);
        
        Set set = container.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) 
       	{
          Map.Entry nextEntry = (Map.Entry)iterator.next();
          PhoneBook temp = (PhoneBook)nextEntry.getValue();
          temp.print();
        }
    }
}

class Phone implements Comparable
{
    Integer codeNumber, number;

    public Phone(Integer newCodeNumber, Integer number)
    {
        this.codeNumber=newCodeNumber;
        this.number=number;
    }

    @Override
    public int compareTo(Object check) 
    {
        Phone second;
        if(check instanceof Phone)
        {
            second = (Phone)check;
            if(this.codeNumber.compareTo(second.codeNumber) == 0) 
	      return number.compareTo(second.number);
            else 
	      return this.codeNumber.compareTo(second.codeNumber);
        }

        return 1;
    }

    public String FullNumber()
    {
        return "+"+ this.codeNumber.toString() +" "+ this.number.toString();
    }
}

abstract class PhoneBook
{
    public abstract void print();
}

class Person extends PhoneBook
{
    String firstName, lastName, address;
    Phone number;

    public Person(String newFirstName, String newLastName, String newAddress, Phone newNumber)
    {
        this.firstName=newFirstName;
        this.lastName=newLastName;
        this.address=newAddress;
        this.number=newNumber;
    }

    @Override
    public void print() 
    {
        System.out.println("Person: "+ this.firstName +" "+ this.lastName +" Address: "+ this.address +" Phone number: "+ number.FullNumber());
    }
}

class Firm extends PhoneBook
{
    String firstName, address;
    Phone number;

    public Firm(String newFirstName, String newAddress, Phone newNumber)
    {
        this.firstName=newFirstName;
        this.address=newAddress;
        this.number=newNumber;
    }

    @Override
    public void print()
    {
        System.out.println("Firm: "+ this.firstName +" Address: "+ this.address +" Phone number: " + number.FullNumber());
    }

}