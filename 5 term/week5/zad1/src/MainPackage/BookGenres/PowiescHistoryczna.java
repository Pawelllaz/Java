package MainPackage.BookGenres;

import MainPackage.KsiazkaDecorators.Ksiazka;

public class PowiescHistoryczna extends Ksiazka {
    public PowiescHistoryczna(String newAuthor, String newTitle, Integer newNumberOfPages) {
        super(newAuthor, newTitle, newNumberOfPages);
    }

    @Override
    public String toString() {
        return "Powieść Historyczna --> "+super.toString();
    }
}
