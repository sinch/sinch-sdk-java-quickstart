import com.sinch.sdk.SinchClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public abstract class Application {

    private static final String LOGGING_PROPERTIES_FILE = "logging.properties";
    private static final Logger LOGGER = initializeLogger();

    public static void main(String[] args) {
        try {

            SinchClient client = SinchClientHelper.initSinchClient();
            LOGGER.info("Application initiated. SinchClient ready.");

        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }

    static Logger initializeLogger() {
        try (InputStream logConfigInputStream =
                Application.class.getClassLoader().getResourceAsStream(LOGGING_PROPERTIES_FILE)) {

            if (logConfigInputStream != null) {
                LogManager.getLogManager().readConfiguration(logConfigInputStream);
            } else {
                throw new RuntimeException(
                        "The file '%s' couldn't be loaded.".formatted(LOGGING_PROPERTIES_FILE));
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return Logger.getLogger(Application.class.getName());
    }
}
