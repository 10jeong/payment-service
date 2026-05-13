package com.yeoljeong.tripmate.payment.presentation.controller.external;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payments")
public class PaymentRedirectController {

    @GetMapping("/test-page")
    public String redirectToPaymentTestPage() {
        return "forward:/toss-test-payment.html";
    }

}
