package nl.radiantrealm.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nl.radiantrealm.library.http.HttpRequest;
import nl.radiantrealm.library.http.RequestHandler;
import nl.radiantrealm.library.http.StatusCode;
import nl.radiantrealm.library.utils.JsonUtils;
import nl.radiantrealm.library.utils.Result;
import nl.radiantrealm.minecraft.Main;
import nl.radiantrealm.minecraft.auth.CookieUtils;
import nl.radiantrealm.minecraft.cache.PlayerAccountCache;
import nl.radiantrealm.minecraft.cache.SavingsAccountCache;
import nl.radiantrealm.minecraft.cache.SavingsOwnerCache;
import nl.radiantrealm.minecraft.view.BankconomyViewBuilder;

import java.util.Map;
import java.util.UUID;

public class BankconomyViewAPI implements RequestHandler {
    private final PlayerAccountCache playerAccountCache = Main.playerAccountCache;
    private final SavingsAccountCache savingsAccountCache = Main.savingsAccountCache;
    private final SavingsOwnerCache savingsOwnerCache = Main.savingsOwnerCache;

    @Override
    public void handle(HttpRequest request) throws Exception {
        UUID playerUUID = CookieUtils.getSessionTOken(request.exchange());

        if (playerUUID == null) {
            try (request) {
                request.sendStatusResponse(StatusCode.UNAUTHORIZED);
                return;
            }
        }

        JsonObject requestBody = Result.function(() -> {
            try {
                return JsonUtils.getJsonObject(request.getRequestBody());
            } catch (Exception e) {
                return null;
            }
        });

        if (requestBody == null) {
            try (request) {
                request.sendStatusResponse(StatusCode.BAD_REQUEST);
                return;
            }
        }

        JsonObject requestedViews = Result.function(() -> {
            try {
                return JsonUtils.getJsonObject(requestBody, "views");
            } catch (Exception e) {
                return null;
            }
        });

        if (requestedViews == null) {
            try (request) {
                request.sendStatusResponse(StatusCode.BAD_REQUEST);
                return;
            }
        }

        JsonObject responseBody = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : requestedViews.entrySet()) {
            responseBody.add(entry.getKey(), switch (entry.getKey()) {
                case "player_account_overview" -> BankconomyViewBuilder.buildPlayerAccountOverview(playerUUID);
                case "savings_accounts_list" -> BankconomyViewBuilder.buildSavingsAccountsList(playerUUID);
                case "savings_account_detailed" -> getSavingsAccountDetailed(entry.getValue());
                case "recent_savings_transactions" -> getRecentSavingsTransactions(entry.getValue());

                default -> {
                    JsonObject object = new JsonObject();
                    object.addProperty("error", "Unknown view type.");
                    yield object;
                }
            });
        }

        try (request) {
            request.sendResponse(StatusCode.OK, responseBody);
        }
    }

    //The two methods below are quite WET...

    private JsonObject getSavingsAccountDetailed(JsonElement element) throws Exception {
        JsonObject object = Result.function(() -> {
            try {
                return element.getAsJsonObject();
            } catch (Exception e) {
                return null;
            }
        });

        JsonObject result = new JsonObject();

        if (object == null) {
            result.addProperty("error", "Missing input parameters.");
            return result;
        }

        UUID savingsUUID = Result.function(() -> {
            try {
                return JsonUtils.getJsonUUID(object, "savings_uuid");
            } catch (Exception e) {
                return null;
            }
        });

        if (savingsUUID == null) {
            result.addProperty("error", "Missing savings UUID.");
            return result;
        }

        return BankconomyViewBuilder.buildSavingsAccountDetailed(savingsUUID);
    }

    private JsonObject getRecentSavingsTransactions(JsonElement element) throws Exception {
        JsonObject object = Result.function(() -> {
            try {
                return element.getAsJsonObject();
            } catch (Exception e) {
                return null;
            }
        });

        JsonObject result = new JsonObject();

        if (object == null) {
            result.addProperty("error", "Missing input parameters.");
            return result;
        }

        UUID savingsUUID = Result.function(() -> {
            try {
                return JsonUtils.getJsonUUID(object, "savings_uuid");
            } catch (Exception e) {
                return null;
            }
        });

        return BankconomyViewBuilder.recentSavingsTransactions.get(savingsUUID);
    }
}
