package demo.personalcapital;

public class PCFailureResponse implements PCResponse{

    private String message;

    PCFailureResponse(String message){

        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }


}
