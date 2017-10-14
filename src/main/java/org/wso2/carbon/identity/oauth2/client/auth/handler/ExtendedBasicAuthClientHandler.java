/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth2.client.auth.handler;

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.clientauth.BasicAuthClientAuthHandler;

import java.util.Properties;

/**
 * Extended Basic Auth Client Handler allows you to control whether you need strict client validation for
 * Authorization Code Grant type. This is useful in cases where authorization code grant with consumer secret is
 * preferred over Implicit Grant type
 */
public class ExtendedBasicAuthClientHandler extends BasicAuthClientAuthHandler {

    private static final String STRICT_CLIENT_VALIDATION_FOR_AUTHZ_CODE_GRANT =
            "StrictClientCredentialValidationForAuthzCodeGrant";
    private static final String AUTHORIZATION_CODE = "authorization_code";


    /**
     * Configuration property value to decide whether we need to do strict client validation for authorization grant
     * type
     */
    protected boolean strictlyValidateClientForAuthzCodeGrant;


    @Override
    public void init(Properties properties) throws IdentityOAuth2Exception {
        super.init(properties);
        strictlyValidateClientForAuthzCodeGrant = Boolean.parseBoolean(
                properties.getProperty(STRICT_CLIENT_VALIDATION_FOR_AUTHZ_CODE_GRANT, Boolean.TRUE.toString()));
    }

    @Override
    public boolean canAuthenticate(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        OAuth2AccessTokenReqDTO oauth2AccessTokenReqDTO = tokReqMsgCtx.getOauth2AccessTokenReqDTO();

        if (isAuthorizationCodeGrant(oauth2AccessTokenReqDTO) &&
                StringUtils.isEmpty(oauth2AccessTokenReqDTO.getClientSecret()) &&
                !strictlyValidateClientForAuthzCodeGrant) {
            // Client Secret not present for authorization grant request. But we have disabled strict client
            // validation too. So we are good.
            return true;
        } else {
            // We let the super class handle other cases.
            return super.canAuthenticate(tokReqMsgCtx);
        }
    }


    @Override
    public boolean authenticateClient(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        OAuth2AccessTokenReqDTO oauth2AccessTokenReqDTO = tokReqMsgCtx.getOauth2AccessTokenReqDTO();

        if (isAuthorizationCodeGrant(oauth2AccessTokenReqDTO) &&
                StringUtils.isEmpty(oauth2AccessTokenReqDTO.getClientSecret()) &&
                !strictlyValidateClientForAuthzCodeGrant) {
            // No client authentication for Authorization Code Token requests that come without a secret.
            return true;
        } else {
            // We let the super class handle other cases.
            return super.authenticateClient(tokReqMsgCtx);
        }
    }

    private boolean isAuthorizationCodeGrant(OAuth2AccessTokenReqDTO tokenReqDTO) {
        String grantType = tokenReqDTO.getGrantType();
        return StringUtils.equalsIgnoreCase(grantType, AUTHORIZATION_CODE);
    }
}
