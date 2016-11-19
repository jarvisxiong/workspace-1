package ume.pareva.restservices;

/**
 *
 * @author Alex
 */
public enum REST_SUBSCRIPTION_RESPONSE {

    NEWSUBSCRIPTION("SUBSCRIPTION RECORD CREATED SUCCESSFULLY"),
    SUBSCRIPTION_EXIST("SUBSCRIPTION RECORD ALREADY EXISTS"),
    BLOCKED_USER("USER IS BLOCKED SO NOT FORWARDING FOR SUBSCRIPTION"),
    SUBSCRIPTION_UPDATED("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY"),
    SUBSCRIPTION_ERROR("ERROR - CONTACT MADAN/ALEX - madan@umelimited.com/alex.sanchez@umelimited.com");

    private final String description;

    REST_SUBSCRIPTION_RESPONSE(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
