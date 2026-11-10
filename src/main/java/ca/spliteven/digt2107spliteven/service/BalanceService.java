package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentModel;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Works out what each member owes (negative) or is owed (positive) (BS-001, FR-011). */
public class BalanceService {

    private final ExpenseService expenseService;

    public BalanceService(ExpenseService expenseService) {
        this.expenseService = Objects.requireNonNull(expenseService);
    }

    /** The current balance, in cents, of every member of the group. */
    public Map<MemberModel, Long> balancesFor(GroupModel group) {
        Objects.requireNonNull(group, "group");
        // Payments are not recorded anywhere yet (BS-003).
        return calculate(group.getMembers(),
                expenseService.expensesFor(group),
                Collections.emptyList());
    }

    /** Credits each payer, debits each sharer, and applies payments; balances sum to zero. */
    public static Map<MemberModel, Long> calculate(List<MemberModel> members,
            List<ExpenseModel> expenses,
            List<PaymentModel> payments) {
        Map<MemberModel, Long> balances = new LinkedHashMap<>();
        for (MemberModel member : members) {
            balances.put(member, 0L);
        }

        for (ExpenseModel expense : expenses) {
            balances.merge(expense.getPaidBy(), expense.getAmountCents(), Long::sum);
            for (Map.Entry<MemberModel, Long> share : expense.getShares().entrySet()) {
                balances.merge(share.getKey(), -share.getValue(), Long::sum);
            }
        }

        for (PaymentModel payment : payments) {
            balances.merge(payment.getPaidBy(), payment.getAmountCents(), Long::sum);
            balances.merge(payment.getPaidTo(), -payment.getAmountCents(), Long::sum);
        }

        return balances;
    }
}
