package MainPackage.Wydawnictwa;

import MainPackage.BookGenres.Poemat;
import MainPackage.KsiazkaDecorators.Ksiazka;

public class WydawnictwoPoematow extends Wydawnictwo {
    private String author;
    public WydawnictwoPoematow(String author){
        this.author=author;
    }

    @Override
    public Poemat createBook(String title, Integer amountOfPages) {
        return new Poemat(author, title, amountOfPages);
    }
}
