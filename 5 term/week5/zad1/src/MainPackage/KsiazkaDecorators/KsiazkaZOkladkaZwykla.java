package MainPackage.KsiazkaDecorators;

public class KsiazkaZOkladkaZwykla extends KsiazkaDecorator{
    public KsiazkaZOkladkaZwykla(Publikacja newKsiazka){
        super(newKsiazka);
    }

    @Override
    public String toString() {
        return super.toString()+" | Okladka zwykla";
    }
}
