package nl.radiantrealm.minecraft.cache;

import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.library.utils.FormatUtils;
import nl.radiantrealm.minecraft.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SavingsOwnerCache extends CacheRegistry<UUID, Set<UUID>> {

    public SavingsOwnerCache() {
        super(900000);
    }

    @Override
    protected Set<UUID> load(UUID uuid) throws Exception {
        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT savings_uuid FROM bankconomy_savings WHERE owner_uuid = ?"
            );

            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            Set<UUID> set = new HashSet<>();

            while (rs.next()) {
                set.add(FormatUtils.formatUUID(rs, "savings_uuid"));
            }

            return set;
        }
    }
}
