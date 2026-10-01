package com.vnpay.common;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {

    @Test
    void vnpayTimezoneIsAlwaysGmtPlusSeven() {
        assertEquals(7 * 60 * 60 * 1000, Config.VNPAY_TIME_ZONE.getRawOffset());
        assertEquals("Asia/Ho_Chi_Minh", Config.VNPAY_TIME_ZONE.getID());
    }

    @Test
    void hashAllFieldsSortsAndSkipsEmptyValuesWithoutExtraSeparators() {
        Map<String, String> fields = new HashMap<>();
        fields.put("c", "3");
        fields.put("b", "");
        fields.put("a", "1");

        assertEquals(
                Config.hmacSHA512(Config.secretKey, "a=1&c=3"),
                Config.hashAllFields(fields));
    }

    @Test
    void generatedTransactionReferenceHasRequestedNumericLength() {
        String reference = Config.getRandomNumber(12);

        assertTrue(reference.matches("\\d{12}"));
    }

    @Test
    void htmlEscapingCoversCallbackValues() {
        assertEquals("&lt;script&gt;&quot;x&quot; &amp; &#x27;y&#x27;&lt;/script&gt;",
                Config.escapeHtml("<script>\"x\" & 'y'</script>"));
        assertEquals("", Config.escapeHtml(null));
    }
}
