package demo.personalcapital;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
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
public class SearchHandler implements RequestHandler<Map<String,String>, PCResponse> {

    private static final String ELASTIC_SEARCH_URL = "elasticSearchUrl";
    private static final String ELASTIC_SEARCH_INDEX = "elasticSearchIndex";

    @Override
    public PCResponse handleRequest(Map<String, String> input, Context context){

        String elasticSearchUrl = System.getenv(ELASTIC_SEARCH_URL);
        String elasticSearchIndex = System.getenv(ELASTIC_SEARCH_INDEX);
        RestHighLevelClient client = createRestHighLevelClient(elasticSearchUrl);

        //Build the search request to ES
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] ids = new String[input.values().size()];
        input.values().toArray(ids);
        BoolQueryBuilder root = QueryBuilders.boolQuery();
        for(Map.Entry<String,String> entry : input.entrySet()) {
            root.must(QueryBuilders.matchQuery(entry.getKey(),entry.getValue()));
        }

        System.out.println(root.toString(true));
        searchSourceBuilder.query(root);
        SearchRequest esRequest = new SearchRequest(elasticSearchIndex);
        esRequest.source(searchSourceBuilder);

        //Execute the search request to ES and get back the response
        try {
            SearchResponse esResponse = client.search(esRequest, RequestOptions.DEFAULT);
            //Parse the results

            SearchHit[]  hits = esResponse.getHits().getHits();

            System.out.println("Number of hits: "+hits.length);

            List<Map<String,String>> results = Stream.of(hits).parallel().map(this::convertTo).collect(Collectors.toList());

            return PCResponse.createSuccessResponse(results);

        } catch(IOException ioex) {
            ioex.printStackTrace();
            return PCResponse.createFailureResponse("An internal error occured. Could not finish executing search");
        }

    }

    private RestHighLevelClient createRestHighLevelClient(String elasticSearchUrl) {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(HttpHost.create(elasticSearchUrl)));
        return client;
    }

    private Map<String,String> convertTo(SearchHit hit){
        Map<String,String> output = new HashMap<>();
        for(Map.Entry<String,Object> entry : hit.getSourceAsMap().entrySet()) {
            output.put(entry.getKey(),String.valueOf(entry.getValue()));
        }
        return output;
    }

}
