package MainPackage.KsiazkaDecorators;

public class KsiazkaZObwoluta extends KsiazkaDecorator {
    public KsiazkaZObwoluta(Publikacja newKsiazka) throws Exception {
        super(newKsiazka);
        if(!(newKsiazka instanceof KsiazkaZOkladkaZwykla || newKsiazka instanceof KsiazkaZOkladkaTwarda) || newKsiazka instanceof KsiazkaZObwoluta)
            throw new WrongBookException("ksiazka musi miec okladke, obwoluta moze byc tylko jedna");
    }

    @Override
    public String toString(){
        return super.toString()+" | Obwoluta zalożona";
    }
}