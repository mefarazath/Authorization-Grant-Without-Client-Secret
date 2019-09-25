<h1>Authorization Grant without Client Secret</h1>

This repo contains artifacts and configurations required to use authorization grant flow without client_secret for public clients.

Download WSO2 Identity Server at https://wso2.com/identity-and-access-management or for the latest milestone go to https://github.com/wso2/product-is/releases

Steps to try out,

1. Build the jar and place it in WSO2_HOME/repository/components/lib
This jar contains two extension classes
    1. ExtendedAuthzGrantValidator --> Removes the strict client validation requirement for authorization code grant.
     Configuration involved explained in step #2
    2. ExtendedBasicAuthClientHandler -> Overrides the default behaviour of strict client authentication for 
    authorization grant. Configuration involved explained in step #3

2. - Override the default grant validator for Authorization Code Grant type. The default validator enforces client 
authentication and we need to get rid of that.

````
<OAuth>
       ....
     <SupportedGrantTypes>
                <SupportedGrantType>
                    <GrantTypeName>authorization_code</GrantTypeName>
                    <GrantTypeHandlerImplClass>org.wso2.carbon.identity.oauth2.token.handlers.grant.AuthorizationCodeGrantHandler</GrantTypeHandlerImplClass>
                    <GrantTypeValidatorImplClass>org.wso2.carbon.identity.oauth2.grant.validator.ExtendedAuthzGrantValidator</GrantTypeValidatorImplClass>
                </SupportedGrantType>
     </SupportedGrantTypes>
      ....
</OAuth>
````

3. Change the WSO2_HOME/repository/conf/identity/identity.xml as follows.
````
<OAuth>
   ....
       <ClientAuthHandlers>
            <!--ClientAuthHandler Class="org.wso2.carbon.identity.oauth2.token.handlers.clientauth.BasicAuthClientAuthHandler">
                <Property Name="StrictClientCredentialValidation">false</Property>
            </ClientAuthHandler-->
            <ClientAuthHandler Class="org.wso2.carbon.identity.oauth2.client.auth.handler.ExtendedBasicAuthClientHandler">
                <Property Name="StrictClientCredentialValidation">false</Property>
                <Property Name="StrictClientCredentialValidationForAuthzCodeGrant">false</Property>
            </ClientAuthHandler>
        </ClientAuthHandlers>
    ....
</OAuth>
````


4. Startup the server

5. Create an oauth application
````
curl -k -X POST https://localhost:9443/identity/connect/register -H 'authorization: Basic YWRtaW46YWRtaW4=' -H 'content-type: application/json' -d '{"redirect_uris": ["https://localhost/callback"],"client_name": "authz_code_test","ext_param_owner": "application_owner","grant_types": ["authorization_code password client_credentials"]}'
````
Response:
````
{"grant_types":["authorization_code","password","client_credentials"],"client_secret_expires_at":"0","redirect_uris":["https:\/\/localhost\/callback"],"client_secret":"bhf5pVEKOwwMhwyGLJ1mz70mQdYa","client_name":"admin_authz_code_test","client_id":"XALwgcRGsR4zud4RsokMmtNm3xQa"}
````

6. Get an authorization code,
(Refer: https://farasath.blogspot.com/2017/10/oauth2-authorization-code-flow-without.html you can skip the Service Provider 
creation part)
````
https://localhost:9443/oauth2/authorize?response_type=code&client_id=XALwgcRGsR4zud4RsokMmtNm3xQa&redirect_uri=https://localhost/callback&scope=read
````

Response After authentication and consent
````
https://localhost/callback?code=2cb973ea-50de-3dc9-91af-ef4b1978a80f
````

7. Get the access token (Note: we don't sent client secret in the authorization header. Instead we only sent clien_id
 as a request param)
````
curl -k -v -d "grant_type=authorization_code&code=2cb973ea-50de-3dc9-91af-ef4b1978a80f&redirect_uri=https://localhost/callback&client_id=XALwgcRGsR4zud4RsokMmtNm3xQa" https://localhost:9443/oauth2/token 
````
