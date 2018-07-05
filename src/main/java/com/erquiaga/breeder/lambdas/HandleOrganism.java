package com.erquiaga.breeder.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

import static com.erquiaga.breeder.utils.BreederConstants.*;
import static com.erquiaga.breeder.utils.BreederConstants.ORGANISM_FILE_SUFFIX;
import static com.erquiaga.breeder.utils.BreederUtils.*;

public class HandleOrganism extends ApiGatewayProxyLambda {

    //Handle everything under /organism/{id}
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        super.handleRequest(inputStream, outputStream, context);
    }

    @Override
    public JSONObject handleGetRequest(JSONObject jsonEventObject, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Getting Organism");

        JSONObject responseJson = new JSONObject();
        String responseCode = "200";
        String organismId = "";
        try {
            if (jsonEventObject.get("pathParameters") != null) {
                JSONObject pathParameters = (JSONObject)jsonEventObject.get("pathParameters");
                organismId = getParmeterIfExists(pathParameters, ORGANISM_ID_KEY, "");
            }

            if(!"".equals(organismId) && organismExists(organismId)) {
                JSONObject organismJson = getOrganismJson(organismId);

                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", responseCode);
                responseJson.put("body", organismJson.toString());
            } else {
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", "404");
                responseJson.put("body", "Organism not found!");
            }
        } catch (Exception e) {
            logger.log("Exception: " + e.toString());
            responseJson.put("statusCode", "400");
            responseJson.put("exception", e);
        }

        return responseJson;
    }

    @Override
    protected JSONObject handlePutRequest(JSONObject jsonEventObject, Context context) {
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        responseBody.put("message", "This should update an organism");
        responseJson.put("statusCode", 200);
        responseJson.put("body", responseBody.toString());

        return responseJson;
    }

    @Override
    protected JSONObject handleDeleteRequest(JSONObject jsonEventObject, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Deleting Organism");

        JSONObject responseJson = new JSONObject();
        String responseCode = "200";
        String organismId = "";
        try {
            if (jsonEventObject.get("pathParameters") != null) {
                JSONObject pathParameters = (JSONObject)jsonEventObject.get("pathParameters");
                organismId = getParmeterIfExists(pathParameters, ORGANISM_ID_KEY, "");
            }

            if(!"".equals(organismId) && organismExists(organismId)) {
                String organismKey = getOrganismObjectKey(organismId);
                AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
                s3Client.deleteObject(BREEDER_S3_BUCKET, organismKey);

                String bodyMessage = "Deleted Object at this location - S3:" + BREEDER_S3_BUCKET + "/" + organismKey;

                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", responseCode);
                responseJson.put("body", bodyMessage);
            } else {
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", "404");
                responseJson.put("body", "Organism not found!");
            }
        } catch (Exception e) {
            logger.log("Exception: " + e.toString());
            responseJson.put("statusCode", "400");
            responseJson.put("exception", e);
        }

        return responseJson;
    }

    @Override
    protected JSONObject handlePostRequest(JSONObject jsonEventObject, Context context) {
        JSONObject responseJson = new JSONObject();
        JSONObject responseBody = new JSONObject();
        responseBody.put("message", "This method isn't supported");
        responseJson.put("statusCode", 200);
        responseJson.put("body", responseBody.toString());

        return responseJson;
    }
}
