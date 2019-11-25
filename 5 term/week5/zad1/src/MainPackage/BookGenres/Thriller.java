package MainPackage.BookGenres;

import MainPackage.KsiazkaDecorators.Ksiazka;

public class Thriller extends Ksiazka {
    public Thriller(String newAuthor, String newTitle, Integer newNumberOfPages) {
        super(newAuthor, newTitle, newNumberOfPages);
    }

    @Override
    public String toString() {
        return "Thriller --> "+super.toString();
    }
}
