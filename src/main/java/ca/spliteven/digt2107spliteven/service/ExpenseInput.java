package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentMethod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One Add Expense submission, exactly as it came off the form.
 *
 */
public class ExpenseInput {

    private final String amountText;
    private final MemberModel paidBy;
    private final PaymentMethod paymentMethod;
    private final LocalDate date;
    private final String category;
    private final String note;
    private final List<MemberModel> sharedBy;

    public ExpenseInput(String amountText,
            MemberModel paidBy,
            PaymentMethod paymentMethod,
            LocalDate date,
            String category,
            String note,
            List<MemberModel> sharedBy) {
        this.amountText = amountText;
        this.paidBy = paidBy;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.category = category;
        this.note = note;
        this.sharedBy = sharedBy == null
                ? Collections.<MemberModel>emptyList()
                : Collections.unmodifiableList(new ArrayList<>(sharedBy));
    }

    public String getAmountText() {
        return amountText;
    }

    public MemberModel getPaidBy() {
        return paidBy;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public List<MemberModel> getSharedBy() {
        return sharedBy;
    }
}
