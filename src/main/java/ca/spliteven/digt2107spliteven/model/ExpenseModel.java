package ca.spliteven.digt2107spliteven.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * One expense paid by a member and shared by one or more members (GE-002,
 * FR-004).
 */
public class ExpenseModel {

    private final UUID id;
    private final long creationTime;
    private final long amountCents;
    private final PaymentMethod paymentMethod;
    private final LocalDate date;
    private final String category;
    private final String note;
    private final MemberModel paidBy;
    private final List<MemberModel> sharedBy;

    public ExpenseModel(long amountCents,
            PaymentMethod paymentMethod,
            LocalDate date,
            String category,
            String note,
            MemberModel paidBy,
            List<MemberModel> sharedBy) {
        this(UUID.randomUUID(), Instant.now().toEpochMilli(), amountCents,
                paymentMethod, date, category, note, paidBy, sharedBy);
    }

    public ExpenseModel(UUID id,
            long creationTime,
            long amountCents,
            PaymentMethod paymentMethod,
            LocalDate date,
            String category,
            String note,
            MemberModel paidBy,
            List<MemberModel> sharedBy) {
        Objects.requireNonNull(id, "id");
        if (amountCents <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (paidBy == null) {
            throw new IllegalArgumentException("Payer cannot be null");
        }
        if (sharedBy == null || sharedBy.isEmpty()) {
            throw new IllegalArgumentException("An expense must be shared by at least one member");
        }
        this.id = id;
        this.creationTime = creationTime;
        this.amountCents = amountCents;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.category = category.trim();
        this.note = note == null ? "" : note.trim();
        this.paidBy = paidBy;
        this.sharedBy = Collections.unmodifiableList(new ArrayList<>(sharedBy));
    }

    public UUID getId() {
        return id;
    }

    public long getCreationTime() {
        return creationTime;
    }

    /** The total of the expense, in whole cents. */
    public long getAmountCents() {
        return amountCents;
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

    /** The optional note, or an empty string when none was given. */
    public String getNote() {
        return note;
    }

    public MemberModel getPaidBy() {
        return paidBy;
    }

    /** How much each person owes, split equally in cents
     *  If it doesn't divide evenly, the first people each pay one extra cent */
    public Map<MemberModel, Long> getShares() {
        long baseShare = amountCents / sharedBy.size();
        long leftoverCents = amountCents % sharedBy.size();
        Map<MemberModel, Long> shares = new LinkedHashMap<>();
        for (int i = 0; i < sharedBy.size(); i++) {
            shares.put(sharedBy.get(i), baseShare + (i < leftoverCents ? 1 : 0));
        }
        return shares;
    }

    /** The members who share this expense. The list cannot be modified. */
    public List<MemberModel> getSharedBy() {
        return sharedBy;
    }
}
