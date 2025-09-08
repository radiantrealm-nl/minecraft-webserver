package nl.radiantrealm.minecraft.cache;

import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.minecraft.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.UUID;

public class AccountCreationDateCache extends CacheRegistry<UUID, Long> {

    public AccountCreationDateCache() {
        super(Duration.ofHours(6));
    }

    @Override
    protected Long load(UUID uuid) throws Exception {
        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT `timestamp` FROM bankconomy_audits WHERE related_uuid = ? AND audit_type IN (CREATE_PLAYER_ACCOUNT, CREATE_SAVINGS_ACCOUNT)"
            );

            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getLong("timestamp");
            }

            return null;
        }
    }
}
