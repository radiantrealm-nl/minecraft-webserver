package nl.radiantrealm.minecraft;

import nl.radiantrealm.minecraft.cache.AccountCreationDateCache;
import nl.radiantrealm.minecraft.cache.PlayerAccountCache;
import nl.radiantrealm.minecraft.cache.SavingsAccountCache;
import nl.radiantrealm.minecraft.cache.SavingsOwnerCache;

public class Main {
    public static final PlayerAccountCache playerAccountCache = new PlayerAccountCache();
    public static final SavingsAccountCache savingsAccountCache = new SavingsAccountCache();
    public static final SavingsOwnerCache savingsOwnerCache = new SavingsOwnerCache();
    public static final AccountCreationDateCache accountCreationDateCache = new AccountCreationDateCache();

    public static final Database database = new Database();

    public static void main(String[] args) {
    }
}
