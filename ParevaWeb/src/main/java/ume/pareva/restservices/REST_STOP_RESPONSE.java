package ume.pareva.restservices;

/**
 *
 * @author Alex
 */
public enum REST_STOP_RESPONSE {

    SUCCESS("Subscription Successfully Stopped"),
    FAILURE_INACTIVE("User Already Inactive"),  
    FAILURE_ERROR("System Error");

    private final String description;

    REST_STOP_RESPONSE(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
