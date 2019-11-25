package MainPackage.Wydawnictwa;

import MainPackage.BookGenres.Poemat;
import MainPackage.KsiazkaDecorators.Ksiazka;

public class WydawnictwoPowiesciHistorycznych extends Wydawnictwo {
    private String author;
    public WydawnictwoPowiesciHistorycznych(String author){
        this.author=author;
    }

    @Override
    public Poemat createBook(String title, Integer amountOfPages) {
        return new Poemat(author, title, amountOfPages);
    }
}
