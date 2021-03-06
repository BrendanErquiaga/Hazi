package com.erquiaga.organism.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.erquiaga.organism.utils.OrganismConstants.ORGANISM_S3_BUCKET;
import static com.erquiaga.organism.utils.OrganismConstants.ORGANISM_FOLDER;
import static com.erquiaga.organism.utils.OrganismConstants.ORGANISM_TYPE_KEY;
import static com.erquiaga.organism.utils.OrganismRequestUtils.getParmeterIfExists;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

public class ListOrganisms extends ApiGatewayProxyLambda {

    //Handle GET under /organism
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        super.handleRequest(inputStream, outputStream, context);
    }

    @Override
    public JSONObject handleGetRequest(JSONObject jsonEventObject, Context context)
    {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling GET request");
        JSONObject responseJson = new JSONObject();
        int responseCode = SC_OK;
        String organismType = "";

        try {
            if (jsonEventObject.get("queryStringParameters") != null) {
                JSONObject queryStringParameters = (JSONObject)jsonEventObject.get("queryStringParameters");
                organismType = getParmeterIfExists(queryStringParameters, ORGANISM_TYPE_KEY, "");
            }
            String message = "This should list all organisms.";
            String listPrefix = ORGANISM_FOLDER;
            JSONObject responseBody = new JSONObject();
            JSONArray organismList = new JSONArray();

            if(!"".equals(organismType)) {
                message = "This should list organisms of type: " + organismType;
                listPrefix += "/" + organismType;
            }

            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            ObjectListing organisms = s3Client.listObjects(ORGANISM_S3_BUCKET, listPrefix);

            for(S3ObjectSummary organismObjectSummary : organisms.getObjectSummaries()) {
                String organismKey = organismObjectSummary.getKey();
                if(!organismKey.equals(ORGANISM_FOLDER)) {
                    organismKey = organismKey.substring(0, organismKey.length() - 5);
                    organismKey = organismKey.substring(ORGANISM_FOLDER.length(), organismKey.length());

                    organismList.add(organismKey);
                }
            }

            responseBody.put("organisms", organismList);
            responseBody.put("message", message);

            responseJson.put("isBase64Encoded", false);
            responseJson.put("statusCode", responseCode);
            responseJson.put("body", responseBody.toString());

        } catch (Exception e) {
            logger.log("Exception: " + e.toString());
            responseJson.put("statusCode", SC_BAD_REQUEST);
            responseJson.put("exception", e);
        }

        return responseJson;
    }
}
