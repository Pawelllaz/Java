package MainPackage.KsiazkaDecorators;

public class Ksiazka implements Publikacja{
    private String author;
    private String title;
    private Integer numberOfPages;

    public Ksiazka(String newAuthor, String newTitle, Integer newNumberOfPages){
        author=newAuthor;
        title=newTitle;
        numberOfPages=newNumberOfPages;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public String toString(){
        return getAuthor() + " | " + getTitle() + " | " + getNumberOfPages();
    }
}
