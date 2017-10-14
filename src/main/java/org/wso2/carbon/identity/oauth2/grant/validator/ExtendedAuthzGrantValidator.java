package org.wso2.carbon.identity.oauth2.grant.validator;

import org.apache.oltu.oauth2.as.validator.AuthorizationCodeValidator;

public class ExtendedAuthzGrantValidator extends AuthorizationCodeValidator {
    public ExtendedAuthzGrantValidator() {
        super();
        this.enforceClientAuthentication = false;
    }
}
