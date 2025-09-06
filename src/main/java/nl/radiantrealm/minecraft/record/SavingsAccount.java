package nl.radiantrealm.minecraft.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.utils.DataObject;
import nl.radiantrealm.library.utils.JsonUtils;

import java.math.BigDecimal;
import java.util.UUID;

public record SavingsAccount(UUID savingsUUID, UUID ownerUUID, BigDecimal savingsBalance, BigDecimal accumulatedInterest, String savingsName) implements DataObject {

    public SavingsAccount(JsonObject object) throws IllegalArgumentException {
        this(
                JsonUtils.getJsonUUID(object, "savings_uuid"),
                JsonUtils.getJsonUUID(object, "owner_uuid"),
                JsonUtils.getJsonBigDecimal(object, "savings_balance"),
                JsonUtils.getJsonBigDecimal(object, "accumulated_interest"),
                JsonUtils.getJsonString(object, "savings_name")
        );
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("savings_uuid", savingsUUID.toString());
        object.addProperty("owner_uuid", ownerUUID.toString());
        object.addProperty("savings_balance", savingsBalance);
        object.addProperty("accumulated_interest", accumulatedInterest);
        object.addProperty("savings_name", savingsName);
        return object;
    }
}
