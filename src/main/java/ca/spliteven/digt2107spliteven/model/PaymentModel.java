package ca.spliteven.digt2107spliteven.model;

/** Money paid by one member to another to settle a debt, in whole cents. */
public class PaymentModel {
    private final MemberModel paidBy;
    private final MemberModel paidTo;
    private final long amountCents;

    public PaymentModel(MemberModel paidBy, MemberModel paidTo, long amountCents) {
        if (paidBy == null || paidTo == null) {
            throw new IllegalArgumentException("A payment needs a payer and a receiver");
        }
        if (paidBy == paidTo) {
            throw new IllegalArgumentException("A member cannot pay themselves");
        }
        if (amountCents <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.paidBy = paidBy;
        this.paidTo = paidTo;
        this.amountCents = amountCents;
    }

    public MemberModel getPaidBy() {
        return paidBy;
    }

    public MemberModel getPaidTo() {
        return paidTo;
    }

    public long getAmountCents() {
        return amountCents;
    }
}
