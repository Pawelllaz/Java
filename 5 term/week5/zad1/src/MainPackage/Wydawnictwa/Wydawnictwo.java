package MainPackage.Wydawnictwa;

import MainPackage.KsiazkaDecorators.Ksiazka;

public abstract class Wydawnictwo {
    public static Wydawnictwo getInstance(String str){
        if(str.equals("Józef Ignacy Kraszewski"))
            return new WydawnictwoPowiesciHistorycznych(str);
        if(str.equals("jakis thriller"))
            return new WydawnictwoThrillerów(str);
        if(str.equals("jakis poemat"))
            return new WydawnictwoPowiesciHistorycznych(str);

        return null;
    }

    public abstract Ksiazka createBook(String title, Integer amountOfPages);
}
