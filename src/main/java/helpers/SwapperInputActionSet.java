package helpers;

import com.badlogic.gdx.Input;
import com.megacrit.cardcrawl.helpers.input.InputAction;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;

public class SwapperInputActionSet
{
    public static InputAction swapCard;

    private static final String SWAP_CARD_KEY = "hydrologistmod:SWAP_CARD";

    public static void load()
    {
        swapCard = new InputAction(InputActionSet.prefs.getInteger(SWAP_CARD_KEY, Input.Keys.SPACE));
    }

    public static void save()
    {
        InputActionSet.prefs.putInteger(SWAP_CARD_KEY, swapCard.getKey());
    }

    public static void resetToDefaults()
    {
        swapCard.remap(Input.Keys.SPACE);
    }
}