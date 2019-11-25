package MainPackage.KsiazkaDecorators;

class WrongBookException extends Exception{
    public WrongBookException(String msg){
        super(msg);
    }
}