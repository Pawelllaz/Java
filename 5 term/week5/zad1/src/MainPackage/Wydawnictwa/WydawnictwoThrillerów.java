package MainPackage.Wydawnictwa;

import MainPackage.BookGenres.Poemat;
import MainPackage.KsiazkaDecorators.Ksiazka;

public class WydawnictwoThrillerów extends Wydawnictwo {
    private String author;
    public WydawnictwoThrillerów(String author){
        this.author=author;
    }

    @Override
    public Poemat createBook(String title, Integer amountOfPages) {
        return new Poemat(author, title, amountOfPages);
    }
}
