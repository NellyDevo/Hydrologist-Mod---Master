package hydrologistmod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.interfaces.ApplyPowersForHydrologistPower;
import hydrologistmod.patches.HydrologistTags;

import java.util.HashMap;

public abstract class AbstractHydrologistCard extends CustomCard {
    static final String ICE_LARGE_ORB = "hydrologistmod/images/1024/card_hydrologist_ice_orb.png";
    static final String WATER_LARGE_ORB = "hydrologistmod/images/1024/card_hydrologist_water_orb.png";
    static final String STEAM_LARGE_ORB = "hydrologistmod/images/1024/card_hydrologist_steam_orb.png";
    static final String ICE_SMALL_ORB = "hydrologistmod/images/512/card_hydrologist_orb_ice_small.png";
    static final String WATER_SMALL_ORB = "hydrologistmod/images/512/card_hydrologist_orb_water_small.png";
    static final String STEAM_SMALL_ORB = "hydrologistmod/images/512/card_hydrologist_orb_steam_small.png";
    private static HashMap<CardTags, String> smallOrbMap;
    private static HashMap<CardTags, String> largeOrbMap;

    private static void makeOrbMap() {
        smallOrbMap = new HashMap<>();
        largeOrbMap = new HashMap<>();
        smallOrbMap.put(HydrologistTags.ICE, ICE_SMALL_ORB);
        largeOrbMap.put(HydrologistTags.ICE, ICE_LARGE_ORB);
        smallOrbMap.put(HydrologistTags.WATER, WATER_SMALL_ORB);
        largeOrbMap.put(HydrologistTags.WATER, WATER_LARGE_ORB);
        smallOrbMap.put(HydrologistTags.STEAM, STEAM_SMALL_ORB);
        largeOrbMap.put(HydrologistTags.STEAM, STEAM_LARGE_ORB);
    }

    public AbstractHydrologistCard(String id, String name, String img, int cost, String rawDescription,
                                   CardType type, CardColor color,
                                   CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        if (smallOrbMap == null) {
            makeOrbMap();
        }
    }
    }

    @Override
    public void applyPowers() { //TODO do this properly with a full patch, or wait for overload implementation
        int damageHolder = baseDamage;
        int blockHolder = baseBlock;
        int magicHolder = baseMagicNumber;
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof ApplyPowersForHydrologistPower) {
                ((ApplyPowersForHydrologistPower)p).beforeApplyPowers(this);
            }
        }
        super.applyPowers();
        baseDamage = damageHolder;
        baseBlock = blockHolder;
        baseMagicNumber = magicHolder;
        isDamageModified = baseDamage != damage;
        isBlockModified = baseBlock != block;
        isMagicNumberModified = baseMagicNumber != magicNumber;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int damageHolder = baseDamage;
        int blockHolder = baseBlock;
        int magicHolder = baseMagicNumber;
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof ApplyPowersForHydrologistPower) {
                ((ApplyPowersForHydrologistPower)p).beforeApplyPowers(this);
            }
        }
        super.calculateCardDamage(mo);
        baseDamage = damageHolder;
        baseBlock = blockHolder;
        baseMagicNumber = magicHolder;
        isDamageModified = baseDamage != damage;
        isBlockModified = baseBlock != block;
        isMagicNumberModified = baseMagicNumber != magicNumber;
    }
}
