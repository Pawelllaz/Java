import javax.management.InstanceNotFoundException;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.TreeMap;

public class program7_2
{
    public static void main(String[] args) 
    {
        Firm f1 = new Firm("dobrafirma", "zielona 1", new Phone(48, 111222333));
        Firm f2 = new Firm("zlafirma", "czerwona 2", new Phone(48, 444555666));
      	Firm f3 = new Firm("posredniafirma", "niebieska 3", new Phone(48, 777888999));       	 
       	Person p1 = new Person("Janusz", "Kowalski", "zolta 4", new Phone(48, 123456789));
        Person p2 = new Person("Pawel", "Lazicki", "biala 5", new Phone(48, 987654321));
        Person p3 = new Person("Krzysztof", "Nowak", "czarna 6", new Phone(48, 147852369));

        TreeMap<String, PhoneBook> container = new TreeMap<String, PhoneBook>();
        //container.put(f1.number, f1);
        //container.put(f2.number, f2);
        //container.put(f3.number, f3);
        container.put(p1.firstName, p1);
        container.put(p2.firstName, p2);
        container.put(p3.firstName, p3);
        
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

class Phone
{
    Integer codeNumber, number;

    public Phone(Integer newCodeNumber, Integer number)
    {
        this.codeNumber=newCodeNumber;
        this.number=number;
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

class Person extends PhoneBook implements Comparable
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
    public int compareTo(Object check) 
    {
        Person second;
        if(check instanceof Person)
        {
            second = (Person)check;

            if(this.firstName.compareTo(second.firstName) == 0) 
	      return lastName.compareTo(second.lastName);
            else 
	      return this.firstName.compareTo(second.firstName);
        }

        return 1;
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
        System.out.println("Company: "+ this.firstName +" Address: "+ this.address +" Phone number: "+ number.FullNumber());
    }

}