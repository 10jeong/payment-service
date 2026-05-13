package com.yeoljeong.tripmate.payment.presentation.controller.external;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payments")
public class PaymentDemoController {

    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam BigDecimal amount
    ) {
        return """
            <html>
            <body>
                <h1>토스 결제창 인증 성공</h1>

                <hr/>

                <pre style="background:#f4f4f4; padding:16px; border-radius:8px;">
{
  "paymentKey": "%s",
  "tossOrderId": "%s",
  "amount": %s
}
                </pre>

                <hr/>
            </body>
            </html>
            """.formatted(
                paymentKey,
                orderId,
                amount
        );
    }

    @GetMapping("/fail")
    public String paymentFail(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String orderId
    ) {
        return """
            <html>
            <body>
                <h1>결제 실패</h1>
                <p>실패 코드: %s</p>
                <p>실패 사유: %s</p>
                <p>토스 주문 ID: %s</p>
            </body>
            </html>
            """.formatted(code, message, orderId);
    }
}
