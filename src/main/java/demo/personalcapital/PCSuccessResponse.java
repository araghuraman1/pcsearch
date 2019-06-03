package demo.personalcapital;

import java.util.List;
import java.util.Map;

public class PCSuccessResponse implements PCResponse {

    private List<Map<String,String>> results;

    PCSuccessResponse(List<Map<String,String>> results) {
        this.results = results;
    }

    public List<Map<String,String>> getResults(){
        return this.results;
    }

    public void setResults(List<Map<String,String>> results) {
        this.results = results;
    }

    public int getNumResults(){
        return this.results.size();
    }
}
