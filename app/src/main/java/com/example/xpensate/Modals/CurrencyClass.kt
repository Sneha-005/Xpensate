package com.example.xpensate.Modals

data class CurrencyClass(
    val currencies: Map<String, CurrencyS>
) {

    companion object {
        fun getDefaultCurrencies(): CurrencyClass {
            return CurrencyClass(
                currencies = mapOf(
                    "AED" to CurrencyS("د.إ."),
                    "AFN" to CurrencyS("؋"),
                    "ALL" to CurrencyS("Lek"),
                    "AMD" to CurrencyS("դր."),
                    "ARS" to CurrencyS("$"),
                    "AUD" to CurrencyS("$"),
                    "AZN" to CurrencyS("₼"),
                    "BAM" to CurrencyS("KM"),
                    "BDT" to CurrencyS("৳"),
                    "BGN" to CurrencyS("лв."),
                    "BHD" to CurrencyS("د.ب"),
                    "BIF" to CurrencyS("Fr"),
                    "BND" to CurrencyS("$"),
                    "BOB" to CurrencyS("Bs."),
                    "BRL" to CurrencyS("R$"),
                    "BWP" to CurrencyS("P"),
                    "BYN" to CurrencyS("Br"),
                    "BZD" to CurrencyS("BZ$"),
                    "CAD" to CurrencyS("$"),
                    "CDF" to CurrencyS("Fr"),
                    "CHF" to CurrencyS("CHF"),
                    "CLP" to CurrencyS("$"),
                    "CNY" to CurrencyS("¥"),
                    "COP" to CurrencyS("$"),
                    "CRC" to CurrencyS("₡"),
                    "CVE" to CurrencyS("Esc"),
                    "CZK" to CurrencyS("Kč"),
                    "DJF" to CurrencyS("Fr"),
                    "DKK" to CurrencyS("kr."),
                    "DOP" to CurrencyS("RD$"),
                    "DZD" to CurrencyS("دج"),
                    "EEK" to CurrencyS("kr"),
                    "EGP" to CurrencyS("ج.م"),
                    "ERN" to CurrencyS("Nfk"),
                    "ETB" to CurrencyS("ታብላ"),
                    "EUR" to CurrencyS("€"),
                    "GBP" to CurrencyS("£"),
                    "GEL" to CurrencyS("ლ"),
                    "GHS" to CurrencyS("₵"),
                    "GNF" to CurrencyS("Fr"),
                    "GTQ" to CurrencyS("Q"),
                    "HKD" to CurrencyS("$"),
                    "HNL" to CurrencyS("L"),
                    "HRK" to CurrencyS("kn"),
                    "HUF" to CurrencyS("Ft"),
                    "IDR" to CurrencyS("Rp"),
                    "ILS" to CurrencyS("₪"),
                    "INR" to CurrencyS("₹"),
                    "IQD" to CurrencyS("ع.د"),
                    "IRR" to CurrencyS("﷼"),
                    "ISK" to CurrencyS("kr"),
                    "JMD" to CurrencyS("J$"),
                    "JOD" to CurrencyS("د.ا"),
                    "JPY" to CurrencyS("¥"),
                    "KES" to CurrencyS("Sh"),
                    "KHR" to CurrencyS("៛"),
                    "KMF" to CurrencyS("Fr"),
                    "KRW" to CurrencyS("₩"),
                    "KWD" to CurrencyS("د.ك"),
                    "KZT" to CurrencyS("₸"),
                    "LBP" to CurrencyS("ل.ل"),
                    "LKR" to CurrencyS("Rs"),
                    "LTL" to CurrencyS("Lt"),
                    "LVL" to CurrencyS("Ls"),
                    "LYD" to CurrencyS("د.ل"),
                    "MAD" to CurrencyS("د.م."),
                    "MDL" to CurrencyS("lei"),
                    "MGA" to CurrencyS("Ar"),
                    "MKD" to CurrencyS("ден"),
                    "MMK" to CurrencyS("K"),
                    "MOP" to CurrencyS("MOP$"),
                    "MUR" to CurrencyS("₨"),
                    "MXN" to CurrencyS("$"),
                    "MYR" to CurrencyS("RM"),
                    "MZN" to CurrencyS("MT"),
                    "NAD" to CurrencyS("N$"),
                    "NGN" to CurrencyS("₦"),
                    "NIO" to CurrencyS("C$"),
                    "NOK" to CurrencyS("kr"),
                    "NPR" to CurrencyS("रू"),
                    "NZD" to CurrencyS("$"),
                    "OMR" to CurrencyS("ر.ع."),
                    "PAB" to CurrencyS("B/."),
                    "PEN" to CurrencyS("S/"),
                    "PHP" to CurrencyS("₱"),
                    "PKR" to CurrencyS("₨"),
                    "PLN" to CurrencyS("zł"),
                    "PYG" to CurrencyS("₲"),
                    "QAR" to CurrencyS("ر.ق"),
                    "RON" to CurrencyS("lei"),
                    "RSD" to CurrencyS("дин."),
                    "RUB" to CurrencyS("₽"),
                    "RWF" to CurrencyS("Fr"),
                    "SAR" to CurrencyS("ر.س"),
                    "SDG" to CurrencyS("ج.س."),
                    "SEK" to CurrencyS("kr"),
                    "SGD" to CurrencyS("$"),
                    "SOS" to CurrencyS("Sh"),
                    "SYP" to CurrencyS("ل.س"),
                    "THB" to CurrencyS("฿"),
                    "TND" to CurrencyS("د.ت"),
                    "TOP" to CurrencyS("T$"),
                    "TRY" to CurrencyS("₺"),
                    "TTD" to CurrencyS("TT$"),
                    "TWD" to CurrencyS("NT$"),
                    "TZS" to CurrencyS("Sh"),
                    "UAH" to CurrencyS("₴"),
                    "UGX" to CurrencyS("Sh"),
                    "USD" to CurrencyS("$"),
                    "UYU" to CurrencyS("$"),
                    "UZS" to CurrencyS("лв"),
                    "VEF" to CurrencyS("Bs.F."),
                    "VND" to CurrencyS("₫"),
                    "XAF" to CurrencyS("Fr"),
                    "XOF" to CurrencyS("Fr"),
                    "YER" to CurrencyS("ر.ي"),
                    "ZAR" to CurrencyS("R"),
                    "ZMK" to CurrencyS("ZK"),
                    "ZWL" to CurrencyS("Z$")
                )
            )
        }
    }
}
data class CurrencyS(
    val symbol_native: String
)
fun getSymbol(shortForm: String,currencyClass: CurrencyClass):String? {
    return currencyClass.currencies[shortForm]?.symbol_native
}

