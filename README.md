# http-client
A generic HTTP client written in Kotlin

## Technologies used
1. Java 1.8
2. Kotlin 1.2.71
3. Gson 2.8.5
4. Apache Commons IO 2.6

## Install
Add the following depency to your Maven project pom.xml
```xml
<dependency>
  <groupId>com.github.final60</groupId>
  <artifactId>http-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Features
1. HTTP Basic authentication.
2. OAuth2 authentication.
3. Deserializes the response body to JSON by default, but can return a String of the response body instead.
4. Supports GET, PUT, POST, PATH, DELETE HTTP methods.
5. Supply object to be serialized to JSON, and deserializes JSON to the specified type.
6. Supply request body parameters instead of JSON.

## How to use

### Simple client with no authentication
```java
val httpClient = HttpClientBuilder().uri("https://url.com/api/v1/").build()
val response = httpClient.get("get-all", List::class.java)
```
This example demonstrates the most simple client usage. Here we simply specify the base URI when building the HTTP client. Once we have the client we call the `get()` method, passing the endpoint and the Type of object we want the resulting JSON payload to be deserialized into.

### HTTP Basic authentication
```java
val httpClient = HttpClientBuilder().uri("https://url.com/api/v1/")
                                    .authenticationProvider(BasicAuthenticationProvider()
                                        .username("john123")
                                        .password("password")
                                        .build())
                                    .build()
                                    
val response = httpClient.get("get-all", List::class.java)
```
In this example we are building the HTTP client with a HTTP Basic authenticator, supplying it a valid username and password. 

### OAuth2 authentication
```java
val httpClient = HttpClientBuilder().uri("https://url.com/api/v1/")
                                    .header("Content-Type", "application/x-www-form-urlencoded")
                                    .header("User-Agent", "Example")
                                    .authenticationProvider(OauthAuthenticationProvider()
                                        .clientId("123wdd3131w1")
                                        .clientSecret("1231wq1d12dwqdvg2f")
                                        .uri("https://oauth.url.com")
                                        .redirectUri("https://redirect.url.com")
                                        .headers(createHeaders())
                                        .buildWithAuthorizationToken("98798yduhqwd977q9jo"))
                                    .build()
                                            
val response = httpClient.get("me", Map::class.java)
```
In this example we are building the HTTP client with a OAuth2 authenticator, and individually adding HTTP headers. We are also building up the OAuth2 authenticator with the standard OAuth2 parameters, but also we are supplying specific HTTP headers and a URI required just during authentication. 
