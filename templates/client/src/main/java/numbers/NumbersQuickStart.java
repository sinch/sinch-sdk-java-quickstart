package numbers;

import com.sinch.sdk.domains.numbers.NumbersService;

public class NumbersQuickStart {

    private final NumbersService numbersService;

    public NumbersQuickStart(NumbersService numbersService) {
        this.numbersService = numbersService;

        // replace by your code and business logic
        Snippet.execute(this.numbersService);
    }
}
