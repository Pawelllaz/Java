package MainPackage.BookGenres;

import MainPackage.KsiazkaDecorators.Ksiazka;

public class Poemat extends Ksiazka {
    public Poemat(String newAuthor, String newTitle, Integer newNumberOfPages) {
        super(newAuthor, newTitle, newNumberOfPages);
    }

    @Override
    public String toString() {
        return "Poemat --> "+super.toString();
    }
}
