package numbers;

import com.sinch.sdk.domains.numbers.api.v1.NumbersService;
import com.sinch.sdk.domains.numbers.models.v1.NumberType;
import com.sinch.sdk.domains.numbers.models.v1.request.AvailableNumberListRequest;
import com.sinch.sdk.domains.numbers.models.v1.response.AvailableNumberListResponse;
import java.util.logging.Logger;

public class Snippet {

  private static final Logger LOGGER = Logger.getLogger(Snippet.class.getName());

  static void execute(NumbersService numbersService) {

    String regionCode = "US";
    NumberType type = NumberType.LOCAL;

    AvailableNumberListRequest parameters =
        AvailableNumberListRequest.builder().setRegionCode(regionCode).setType(type).build();

    AvailableNumberListResponse response = numbersService.searchForAvailableNumbers(parameters);

    response
        .iterator()
        .forEachRemaining(
            number -> LOGGER.info(String.format("Available number details: %s", number)));
  }
}
