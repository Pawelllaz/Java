package MainPackage.KsiazkaDecorators;

public abstract class KsiazkaDecorator implements Publikacja{

    Publikacja ksiazka;
    KsiazkaDecorator(Publikacja newKsiazka){
        ksiazka=newKsiazka;
    }

    @Override
    public String getAuthor() {
        return ksiazka.getAuthor();
    }

    @Override
    public String getTitle() {
        return ksiazka.getTitle();
    }

    @Override
    public Integer getNumberOfPages() {
        return ksiazka.getNumberOfPages();
    }

    public String toString(){
        return ksiazka.toString();
    }
}
