namespace payment.Enums
{
    public enum PaymentMethod
    {
        COD,
        MOMO,
        VNPAY,
        BANK_TRANSFER,
        PAYPAL,
        CREDIT_CARD
    }

    public enum PaymentStatus
    {
        Pending,
        Paid,
        Failed,
        Canceled
    }
}
