package demo.personalcapital;

import java.util.List;
import java.util.Map;

public class PCSuccessResponse implements PCResponse {

    public int getNumResults(){
        return this.results.size();
    }

    private List<Map<String,String>> results;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    private String userToken;

    PCSuccessResponse(List<Map<String,String>> results, String userToken) {
        this.results = results;
        this.userToken = userToken;
    }

    public List<Map<String,String>> getResults(){
        return this.results;
    }

    public void setResults(List<Map<String,String>> results) {
        this.results = results;
    }


}
