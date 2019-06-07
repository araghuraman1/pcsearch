package demo.personalcapital;

import java.util.List;
import java.util.Map;

public interface PCResponse {

    static PCResponse createSuccessResponse(List<Map<String,String>> results, String userToken) {
        return new PCSuccessResponse(results,userToken);
    }
}
