package org.wso2.carbon.identity.oauth2.grant.validator;

import org.apache.oltu.oauth2.as.validator.AuthorizationCodeValidator;

/**
 * Extended Grant Validator for Authorization Grant Type to remove the strict client authentication. This will be
 * required to enable authorization code grant without client_secret.
 */
public class ExtendedAuthzGrantValidator extends AuthorizationCodeValidator {
    public ExtendedAuthzGrantValidator() {
        super();
        this.enforceClientAuthentication = false;
    }
}
