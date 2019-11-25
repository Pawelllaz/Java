package MainPackage.KsiazkaDecorators;

public class KsiazkaZOkladkaTwarda extends KsiazkaDecorator{
    public KsiazkaZOkladkaTwarda(Publikacja newKsiazka){
        super(newKsiazka);
    }

    @Override
    public String toString() {
        return super.toString()+" | Okladka twarda";
    }
}
