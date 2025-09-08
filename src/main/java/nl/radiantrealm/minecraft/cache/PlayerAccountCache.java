package nl.radiantrealm.minecraft.cache;

import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.library.utils.FormatUtils;
import nl.radiantrealm.minecraft.Database;
import nl.radiantrealm.minecraft.record.PlayerAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.*;

public class PlayerAccountCache extends CacheRegistry<UUID, PlayerAccount> {

    public PlayerAccountCache() {
        super(Duration.ofMinutes(15));
    }

    @Override
    protected PlayerAccount load(UUID uuid) throws Exception {
        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM bankconomy_players WHERE player_uuid = ?"
            );

            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new PlayerAccount(
                        uuid,
                        rs.getBigDecimal("player_balance"),
                        rs.getString("player_name")
                );
            }

            return null;
        }
    }

    @Override
    protected Map<UUID, PlayerAccount> load(List<UUID> list) throws Exception {
        String params = String.join(", ", Collections.nCopies(list.size(), "?"));

        try (Connection connection = Database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM bankconomy_players WHERE savings_uuid IN (" + params + ")"
            );

            for (int i = 0; i < list.size(); i++) {
                statement.setString(i + 1, list.get(i).toString());
            }

            ResultSet rs = statement.executeQuery();
            Map<UUID, PlayerAccount> result = new HashMap<>(list.size());

            while (rs.next()) {
                UUID playerUUID = FormatUtils.formatUUID(rs.getString("player_uuid"));

                result.put(playerUUID, new PlayerAccount(
                        playerUUID,
                        rs.getBigDecimal("player_balance"),
                        rs.getString("player_name")
                ));
            }

            return result;
        }
    }
}
