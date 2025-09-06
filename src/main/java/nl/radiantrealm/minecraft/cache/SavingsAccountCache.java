package nl.radiantrealm.minecraft.cache;

import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.library.utils.FormatUtils;
import nl.radiantrealm.minecraft.Database;
import nl.radiantrealm.minecraft.record.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class SavingsAccountCache extends CacheRegistry<UUID, SavingsAccount> {

    public SavingsAccountCache() {
        super(900000);
    }

    @Override
    protected SavingsAccount load(UUID uuid) throws Exception {
        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM bankconomy_savings WHERE savings_uuid = ?"
            );

            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new SavingsAccount(
                        uuid,
                        FormatUtils.formatUUID(rs.getString("owner_uuid")),
                        rs.getBigDecimal("savings_balance"),
                        rs.getBigDecimal("accumulated_interest"),
                        rs.getString("savings_name")
                );
            }

            return null;
        }
    }

    @Override
    protected Map<UUID, SavingsAccount> load(List<UUID> list) throws Exception {
        String params = String.join(", ", Collections.nCopies(list.size(), "?"));

        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM bankconomy_savings WHERE savings_uuid IN (" + params + ")"
            );

            for (int i = 0; i < list.size(); i++) {
                statement.setString(i + 1, list.get(i).toString());
            }

            ResultSet rs = statement.executeQuery();
            Map<UUID, SavingsAccount> result = new HashMap<>(list.size());

            while (rs.next()) {
                UUID savingsUUID = FormatUtils.formatUUID(rs.getString("savings_uuid"));

                result.put(savingsUUID, new SavingsAccount(
                        savingsUUID,
                        FormatUtils.formatUUID(rs.getString("owner_uuid")),
                        rs.getBigDecimal("savings_balance"),
                        rs.getBigDecimal("accumulated_interest"),
                        rs.getString("savings_name")
                ));
            }

            return result;
        }
    }
}
