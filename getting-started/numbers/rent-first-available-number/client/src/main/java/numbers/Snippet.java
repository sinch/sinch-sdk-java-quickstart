package numbers;

import com.sinch.sdk.domains.numbers.api.v1.NumbersService;
import com.sinch.sdk.domains.numbers.models.v1.ActiveNumber;
import com.sinch.sdk.domains.numbers.models.v1.Capability;
import com.sinch.sdk.domains.numbers.models.v1.NumberType;
import com.sinch.sdk.domains.numbers.models.v1.SmsConfiguration;
import com.sinch.sdk.domains.numbers.models.v1.request.AvailableNumberRentAnyRequest;
import java.util.Collections;
import java.util.logging.Logger;

public class Snippet {

  private static final Logger LOGGER = Logger.getLogger(Snippet.class.getName());

  static void execute(NumbersService numbersService) {

    String servicePlanId = "YOUR_service_plan_id";
    String regionCode = "YOUR_region_code";

    Capability capability = Capability.SMS;
    NumberType numberType = NumberType.LOCAL;

    ActiveNumber response =
        numbersService.rentAny(
            AvailableNumberRentAnyRequest.builder()
                .setCapabilities(Collections.singletonList(capability))
                .setType(numberType)
                .setRegionCode(regionCode)
                .setSmsConfiguration(
                    SmsConfiguration.builder().setServicePlanId(servicePlanId).build())
                .build());

    LOGGER.info(String.format("Rented number: %s", response));
  }
}
