package demo.personalcapital;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Results of these tests can be verified against the CSV file at
 * http://askebsa.dol.gov/FOIA%20Files/2017/Latest/F_5500_2017_Latest.zip
 *
 */
public class TestService {

    @Test
    public void testWithPlanNameWildcardAndSponsorState() throws UnirestException {

        HttpResponse<JsonNode> response =
        Unirest.get("https://drm7p18zhf.execute-api.us-east-2.amazonaws.com/default/search")
                .queryString("planname","HEITMAN*")
                .queryString("sponsorstate","KS")
                .asJson();

        Assertions.assertTrue( Integer.parseInt(String.valueOf(response.getBody().getObject().get("numResults")))==1);
    }

    @Test
    public void testWithPlanName() throws UnirestException{
        HttpResponse<JsonNode> response =
                Unirest.get("https://drm7p18zhf.execute-api.us-east-2.amazonaws.com/default/search")
                        .queryString("planname","JVS LTD")
                        .asJson();

        Assertions.assertTrue( Integer.parseInt(String.valueOf(response.getBody().getObject().get("numResults")))==1);

    }

    @Test
    public void testWithSponsorState() throws UnirestException{
        HttpResponse<JsonNode> response =
                Unirest.get("https://drm7p18zhf.execute-api.us-east-2.amazonaws.com/default/search")
                        .queryString("planname","KS")
                        .asJson();

        Assertions.assertTrue( Integer.parseInt(String.valueOf(response.getBody().getObject().get("numResults")))==22);

    }

    @Test
    public void testWithNoParamsResultLimitedTo10() throws UnirestException{
        HttpResponse<JsonNode> response = Assertions.assertDoesNotThrow(()->
                Unirest.get("https://drm7p18zhf.execute-api.us-east-2.amazonaws.com/default/search")
                        .asJson()
        );

        Assertions.assertTrue( Integer.parseInt(String.valueOf(response.getBody().getObject().get("numResults")))==1000);

    }



}
