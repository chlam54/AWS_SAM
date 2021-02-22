package database;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;

public class Create implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    public static final String table_name = "message";
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(new AttributeDefinition(
                        "messageID", ScalarAttributeType.S))
                .withKeySchema(new KeySchemaElement("messageID", KeyType.HASH))
                .withProvisionedThroughput(new ProvisionedThroughput(
                        new Long(10), new Long(10)))
                .withTableName(table_name);

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            String output = "{ \"message\": \"Table created!\" }";

//            CreateTableResult result = ddb.createTable(request);
//            System.out.println(result.getTableDescription().getTableName());

            HashMap<String,AttributeValue> item_values =
                    new HashMap<String, AttributeValue>();

            item_values.put("messageID", new AttributeValue("q1AbcZ2"));

            // for (String[] field : extra_fields) {
            //     item_values.put(field[0], new AttributeValue(field[1]));
            // }

            //Add a New Item to a Table
            ddb.putItem(table_name, item_values);

            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (AmazonServiceException e) {
            return response
                    .withBody("{\"msg\":\""+e.getErrorMessage()+"\"}")
                    .withStatusCode(500);
        }
    }
}
