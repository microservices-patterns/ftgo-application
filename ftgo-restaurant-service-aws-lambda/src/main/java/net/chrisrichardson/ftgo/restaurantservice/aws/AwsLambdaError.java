package net.chrisrichardson.ftgo.restaurantservice.aws;

public class AwsLambdaError {
  private String type;
  private String code;
  private String requestId;
  private String message;

  public AwsLambdaError() {
  }

  public AwsLambdaError(String type, String code, String requestId, String message) {
    this.type = type;
    this.code = code;
    this.requestId = requestId;
    this.message = message;
  }

  public String getType() {
    return type;
  }

  public String getCode() {
    return code;
  }

  public String getRequestId() {
    return requestId;
  }

  public String getMessage() {
    return message;
  }
}
