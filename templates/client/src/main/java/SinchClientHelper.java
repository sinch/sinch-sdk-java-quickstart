import com.sinch.sdk.SinchClient;
import com.sinch.sdk.models.Configuration;
import com.sinch.sdk.models.SMSRegion;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public class SinchClientHelper {

    private static final Logger LOGGER = Logger.getLogger(SinchClientHelper.class.getName());

    private static final String SINCH_PROJECT_ID_KEY = "SINCH_PROJECT_ID";
    private static final String SINCH_KEY_ID_KEY = "SINCH_KEY_ID";
    private static final String SINCH_KEY_SECRET_KEY = "SINCH_KEY_SECRET";

    private static final String APPLICATION_API_KEY = "APPLICATION_API_KEY";
    private static final String APPLICATION_API_SECRET = "APPLICATION_API_SECRET";

    private static final String SMS_SERVICE_PLAN_ID = "SMS_SERVICE_PLAN_ID";
    private static final String SMS_SERVICE_PLAN_TOKEN = "SMS_SERVICE_PLAN_TOKEN";
    private static final String CONFIG_FILE = "config.properties";

    public static SinchClient initSinchClient() {

        LOGGER.info("Initializing client");

        Configuration configuration = getConfiguration();

        return new SinchClient(configuration);
    }

    private static Configuration getConfiguration() {

        Properties properties = loadProperties();

        Configuration.Builder builder = Configuration.builder();

        builder.setSmsRegion(SMSRegion.US);

        manageUnifiedCredentials(properties, builder);
        manageApplicationCredentials(properties, builder);
        manageSmsServicePlanCredentials(properties, builder);

        return builder.build();
    }

    private static Properties loadProperties() {

        Properties properties = new Properties();

        try (InputStream input =
                SinchClientHelper.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                LOGGER.severe(String.format("'%s' file could not be loaded", CONFIG_FILE));
            }
        } catch (IOException e) {
            LOGGER.severe(String.format("Error loading properties from '%s'", CONFIG_FILE));
        }

        return properties;
    }

    static void manageUnifiedCredentials(Properties properties, Configuration.Builder builder) {

        Optional<String> projectId = getConfigValue(properties, SINCH_PROJECT_ID_KEY);
        Optional<String> keyId = getConfigValue(properties, SINCH_KEY_ID_KEY);
        Optional<String> keySecret = getConfigValue(properties, SINCH_KEY_SECRET_KEY);

        projectId.ifPresent(builder::setProjectId);
        keyId.ifPresent(builder::setKeyId);
        keySecret.ifPresent(builder::setKeySecret);
    }

    private static void manageApplicationCredentials(
            Properties properties, Configuration.Builder builder) {

        Optional<String> verificationApiKey = getConfigValue(properties, APPLICATION_API_KEY);
        Optional<String> verificationApiSecret = getConfigValue(properties, APPLICATION_API_SECRET);

        verificationApiKey.ifPresent(builder::setApplicationKey);
        verificationApiSecret.ifPresent(builder::setApplicationSecret);
    }

    private static void manageSmsServicePlanCredentials(
            Properties properties, Configuration.Builder builder) {

        Optional<String> servicePlanId = getConfigValue(properties, SMS_SERVICE_PLAN_ID);
        Optional<String> servicePlanToken = getConfigValue(properties, SMS_SERVICE_PLAN_TOKEN);

        servicePlanId.ifPresent(builder::setSmsServicePlanId);
        servicePlanToken.ifPresent(builder::setSmsApiToken);
    }

    private static Optional<String> getConfigValue(Properties properties, String key) {
        String value =
                null != System.getenv(key) ? System.getenv(key) : properties.getProperty(key);

        // empty value means setting not set
        if (null != value && value.trim().isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(value);
    }
}
