package MainPackage.KsiazkaDecorators;

public class KsiazkaZAutografem extends KsiazkaDecorator{
    String autograph;
    public KsiazkaZAutografem(Publikacja newKsiazka, String autograph) throws Exception {
        super(newKsiazka);
        this.autograph = autograph;
        if(newKsiazka instanceof KsiazkaZAutografem)
            throw new WrongBookException("juz ma autograf");
    }

    @Override
    public String toString() {
        return super.toString()+" | "+autograph;
    }
}
