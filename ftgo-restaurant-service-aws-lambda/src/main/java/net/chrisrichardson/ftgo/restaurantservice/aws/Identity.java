package net.chrisrichardson.ftgo.restaurantservice.aws;

public class Identity {

  private String cognitoIdentityPoolId;
  private String accountId;
  private String cognitoIdentityId;
  private String caller;
  private String apiKey;
  private String sourceIp;
  private String cognitoAuthenticationType;
  private String cognitoAuthenticationProvider;
  private String userArn;
  private String userAgent;
  private String user;

  public String getCognitoIdentityPoolId() {
    return cognitoIdentityPoolId;
  }

  public void setCognitoIdentityPoolId(String cognitoIdentityPoolId) {
    this.cognitoIdentityPoolId = cognitoIdentityPoolId;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getCognitoIdentityId() {
    return cognitoIdentityId;
  }

  public void setCognitoIdentityId(String cognitoIdentityId) {
    this.cognitoIdentityId = cognitoIdentityId;
  }

  public String getCaller() {
    return caller;
  }

  public void setCaller(String caller) {
    this.caller = caller;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getSourceIp() {
    return sourceIp;
  }

  public void setSourceIp(String sourceIp) {
    this.sourceIp = sourceIp;
  }

  public String getCognitoAuthenticationType() {
    return cognitoAuthenticationType;
  }

  public void setCognitoAuthenticationType(String cognitoAuthenticationType) {
    this.cognitoAuthenticationType = cognitoAuthenticationType;
  }

  public String getCognitoAuthenticationProvider() {
    return cognitoAuthenticationProvider;
  }

  public void setCognitoAuthenticationProvider(String cognitoAuthenticationProvider) {
    this.cognitoAuthenticationProvider = cognitoAuthenticationProvider;
  }

  public String getUserArn() {
    return userArn;
  }

  public void setUserArn(String userArn) {
    this.userArn = userArn;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }
}
