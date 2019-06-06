package demo.personalcapital;

public class NoResultsException extends Exception{

    public NoResultsException(){
        super("No results found");
    }

}
