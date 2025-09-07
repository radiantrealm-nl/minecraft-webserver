package nl.radiantrealm.minecraft.view;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.minecraft.Database;
import nl.radiantrealm.minecraft.Main;
import nl.radiantrealm.minecraft.cache.AccountCreationDateCache;
import nl.radiantrealm.minecraft.cache.PlayerAccountCache;
import nl.radiantrealm.minecraft.cache.SavingsAccountCache;
import nl.radiantrealm.minecraft.cache.SavingsOwnerCache;
import nl.radiantrealm.minecraft.record.PlayerAccount;
import nl.radiantrealm.minecraft.record.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankconomyViewBuilder {
    private static final PlayerAccountCache playerAccountCache = Main.playerAccountCache;
    private static final SavingsAccountCache savingsAccountCache = Main.savingsAccountCache;
    private static final SavingsOwnerCache savingsOwnerCache = Main.savingsOwnerCache;
    private static final AccountCreationDateCache accountCreationDateCache = Main.accountCreationDateCache;

    public static final RecentSavingsTransactions recentSavingsTransactions = new RecentSavingsTransactions();

    public static JsonObject buildPlayerAccountOverview(UUID playerUUID) throws Exception {
        PlayerAccount playerAccount = playerAccountCache.get(playerUUID);

        JsonObject object = new JsonObject();
        object.addProperty("player_uuid", playerUUID.toString());
        object.addProperty("player_balance", playerAccount.playerBalance().toString());
        object.addProperty("player_name", playerAccount.playerName());
        return object;
    }

    public static JsonArray buildSavingsAccountsList(UUID ownerUUID) throws Exception {
        Map<UUID, SavingsAccount> savingsAccountMap = savingsAccountCache.get(savingsOwnerCache.get(ownerUUID).stream().toList());

        JsonArray array = new JsonArray();

        for (SavingsAccount savingsAccount : savingsAccountMap.values()) {
            JsonObject object = new JsonObject();
            object.addProperty("savings_uuid", savingsAccount.savingsUUID().toString());
            object.addProperty("savings_balance", savingsAccount.savingsBalance().toString());
            object.addProperty("savings_name", savingsAccount.savingsName());
            array.add(object);
        }

        return array;
    }

    public static JsonObject buildSavingsAccountDetailed(UUID savingsUUID) throws Exception {
        SavingsAccount savingsAccount = savingsAccountCache.get(savingsUUID);

        JsonObject object = savingsAccount.toJson();

        Long creationDate = accountCreationDateCache.get(savingsUUID);

        if (creationDate == null) {
            object.addProperty("creation_date", "0");
        } else {
            object.addProperty("creation_date", creationDate);
        }

        return object;
    }

    public static class RecentSavingsTransactions extends CacheRegistry<UUID, JsonObject> {

        public RecentSavingsTransactions() {
            super(123456);
        }

        @Override
        protected JsonObject load(UUID uuid) throws Exception {
            try (Connection connection = Database.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM bankconomy_transactions WHERE source_uuid = ? OR offset_uuid = ? ORDER BY log_id DESC LIMIT 4"
                );

                statement.setString(1, uuid.toString());
                statement.setString(2, uuid.toString());
                ResultSet rs = statement.executeQuery();

                Map<Integer, JsonObject> map = new HashMap<>();

                while (rs.next()) {
                    int logID = rs.getInt("log_id");

                    JsonObject object = new JsonObject();
                    object.addProperty("log_id", logID);
                    object.addProperty("transaction_type", rs.getString("transaction_type"));
                    object.addProperty("transaction_amount", rs.getString("transaction_amount"));
                    object.addProperty("message", rs.getString("message"));
                    map.put(logID, object);
                }

                JsonObject object = new JsonObject();
                map.forEach((key, value) -> object.add(key.toString(), value));
                return object;
            }
        }
    }
}
