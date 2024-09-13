package com.starkcore.user;

public class MarketPlaceApp extends User{
    static ClassData data = new ClassData(MarketPlaceApp.class, "MarketPlaceApp");

    public final String authorizationId;

    /**
     * MarketPlaceApp object
     * <p>
     * The MarketPlaceApp object is an authentication entity for the SDK that
     * represents your MarketPlace Application, being able to access any authorized Workspace.
     * All requests to the Stark Bank and Stark Infra API must be authenticated via an SDK user,
     * which must have been previously created at the Stark Bank or Stark Infra websites
     * [https://web.sandbox.starkbank.com] or [https://web.starkbank.com]
     * before you can use it in this SDK. MarketplaceApps may be passed as the user parameter on
     * each request or may be defined as the default user at the start (See README).
     * If you are accessing a specific MarketplaceAppAuthorization using MarketplaceApp credentials, you should
     * specify the authorization ID when building the MarketplaceApp object or by request, using
     * the MarketplaceApp.replace(app, authorizationId) function, which creates a copy of the app
     * object with the altered authorization ID. If you are listing authorizations, the
     * authorizationId should be None.
     * <p>
     * Parameters (required):
     * @param environment [string]: environment where the organization is being used. ex: "sandbox" or "production"
     * @param id [string]: unique id required to identify organization. ex: "5656565656565656"
     * @param privateKey [EllipticCurve.Organization()]: PEM string of the private key linked to the organization. 
     * <p>
     * Return:
     * @throws Exception error in the request
     */
    public MarketPlaceApp(String environment, String id, String privateKey) throws Exception {
        super(environment, id, privateKey);
        this.authorizationId = null;
    }

    /**
     * MarketPlaceApp object
     * <p>
     * The MarketPlaceApp object is an authentication entity for the SDK that
     * represents your MarketPlace Application, being able to access any authorized Workspace.
     * All requests to the Stark Bank and Stark Infra API must be authenticated via an SDK user,
     * which must have been previously created at the Stark Bank or Stark Infra websites
     * [https://web.sandbox.starkbank.com] or [https://web.starkbank.com]
     * before you can use it in this SDK. MarketplaceApps may be passed as the user parameter on
     * each request or may be defined as the default user at the start (See README).
     * If you are accessing a specific MarketplaceAppAuthorization using MarketplaceApp credentials, you should
     * specify the authorization ID when building the MarketplaceApp object or by request, using
     * the MarketplaceApp.replace(app, authorizationId) function, which creates a copy of the app
     * object with the altered authorization ID. If you are listing authorizations, the
     * authorizationId should be None.
     * <p>
     * Parameters (required):
     * @param environment [string]: environment where the organization is being used. ex: "sandbox" or "production"
     * @param id [string]: unique id required to identify organization. ex: "5656565656565656"
     * @param privateKey [EllipticCurve.Organization()]: PEM string of the private key linked to the organization. 
     * @param authorizationId [string]: unique id of the accessed MarketplaceAppAuthorization, if any. ex: None or "4848484848484848"
     * <p>
     * Return:
     * @throws Exception error in the request
     */
    public MarketPlaceApp(String environment, String id, String privateKey, String authorizationId) throws Exception {
        super(environment, id, privateKey);
        this.authorizationId = authorizationId;
    }

    public String accessId() {
        if (this.authorizationId != null)
            return "marketplace-app-authorization/" + this.authorizationId;
        return "marketplace-app/" + this.id;
    }

    public static MarketPlaceApp replace(MarketPlaceApp MarketPlaceApp, String authorizationId) throws Exception {
        return new MarketPlaceApp(
            MarketPlaceApp.environment,
            MarketPlaceApp.id,
            MarketPlaceApp.pem,
            authorizationId
        );
    }
}

