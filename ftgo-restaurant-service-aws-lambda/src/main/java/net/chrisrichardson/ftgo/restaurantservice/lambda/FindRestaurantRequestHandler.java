package net.chrisrichardson.ftgo.restaurantservice.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import net.chrisrichardson.ftgo.restaurantservice.aws.ApiGatewayResponse;
import net.chrisrichardson.ftgo.restaurantservice.aws.AwsLambdaError;
import net.chrisrichardson.ftgo.restaurantservice.domain.Restaurant;
import net.chrisrichardson.ftgo.restaurantservice.domain.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static net.chrisrichardson.ftgo.restaurantservice.aws.ApiGatewayResponse.applicationJsonHeaders;
import static net.chrisrichardson.ftgo.restaurantservice.aws.ApiGatewayResponse.buildErrorResponse;

public class FindRestaurantRequestHandler extends AbstractAutowiringHttpRequestHandler {

  @Autowired
  private RestaurantService restaurantService;

  @Override
  protected Class<?> getApplicationContextClass() {
    return CreateRestaurantRequestHandler.class;
  }

  @Override
  protected APIGatewayProxyResponseEvent handleHttpRequest(APIGatewayProxyRequestEvent request, Context context) {
    long restaurantId;
    try {
      restaurantId = Long.parseLong(request.getPathParameters().get("restaurantId"));
    } catch (NumberFormatException e) {
      return makeBadRequestResponse(context);
    }

    Optional<Restaurant> possibleRestaurant = restaurantService.findById(restaurantId);

    return possibleRestaurant
            .map(this::makeGetRestaurantResponse)
            .orElseGet(() -> makeRestaurantNotFoundResponse(context, restaurantId));

  }

  private APIGatewayProxyResponseEvent makeBadRequestResponse(Context context) {
    return buildErrorResponse(new AwsLambdaError(
            "Bad response",
            "400",
            context.getAwsRequestId(),
            "bad response"));
  }

  private APIGatewayProxyResponseEvent makeRestaurantNotFoundResponse(Context context, long restaurantId) {
    return buildErrorResponse(new AwsLambdaError(
                    "No entity found",
                    "404",
                    context.getAwsRequestId(),
                    "Found no restaurant with id " + restaurantId));
  }

  private  APIGatewayProxyResponseEvent makeGetRestaurantResponse(Restaurant restaurant) {
    return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(new GetRestaurantResponse(restaurant.getName()))
                    .setHeaders(applicationJsonHeaders())
                    .build();
  }


}
