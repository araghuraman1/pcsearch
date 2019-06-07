package demo.personalcapital;

import com.amazonaws.services.lambda.runtime.Context;
//import software.amazon.awssdk.services.dynamodb.*;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles a search request from API Gateway
 * API Gateway will only allow searches
 * based on Plan Name, Sponsor Name and Sponsor State
 *
 *
 */
public class SearchHandler  {

    private static final String ELASTIC_SEARCH_URL = "elasticSearchUrl";
    private static final String ELASTIC_SEARCH_INDEX = "elasticSearchIndex";


    public PCResponse handleRequest(PCRequest input, Context context) throws IOException, NoResultsException{


        String elasticSearchUrl = System.getenv(ELASTIC_SEARCH_URL);
        String elasticSearchIndex = System.getenv(ELASTIC_SEARCH_INDEX);
        RestHighLevelClient client = createRestHighLevelClient(elasticSearchUrl);

        //Build the search request to ES
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder root = QueryBuilders.boolQuery();


        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(input.getPlanName()!=null && !input.getPlanName().isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("PLAN_NAME",input.getPlanName()));
        }
        if(input.getSponsorName()!=null && !input.getSponsorName().isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("SPONSOR_DFE_NAME",input.getSponsorName()));
        }

        if(input.getSponsorState()!=null && !input.getSponsorState().isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(input.getSponsorState()," SPONS_DFE_LOC_FORGN_PROV_ST","SPONS_DFE_LOC_US_STATE","SPONS_DFE_MAIL_FORGN_PROV_ST","SPONS_DFE_MAIL_US_STATE"));
        }


        root.filter(boolQueryBuilder);


        System.out.println(root.toString(true));
        searchSourceBuilder.query(root);
        searchSourceBuilder.size(1000);
        SearchRequest esRequest = new SearchRequest(elasticSearchIndex);
        esRequest.source(searchSourceBuilder);


        SearchResponse esResponse = client.search(esRequest, RequestOptions.DEFAULT);





        //Parse the results

        SearchHit[]  hits = esResponse.getHits().getHits();

        System.out.println("Number of hits: "+hits.length);

        if(hits.length == 0) {
            throw new NoResultsException();
        }

        List<Map<String,String>> results = Stream.of(hits).parallel().map(this::convertTo).collect(Collectors.toList());

        return PCResponse.createSuccessResponse(results,input.getUserToken());

    }

    private RestHighLevelClient createRestHighLevelClient(String elasticSearchUrl) {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(HttpHost.create(elasticSearchUrl)));
        return client;
    }

    private Map<String,String> convertTo(SearchHit hit){
        Map<String,String> output = new HashMap<>();
        for(Map.Entry<String,Object> entry : hit.getSourceAsMap().entrySet()) {
            if(entry.getKey()!=null && !entry.getKey().equalsIgnoreCase("message")) {
                output.put(entry.getKey(),String.valueOf(entry.getValue()));
            }

        }
        return output;
    }

}
