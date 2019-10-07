package helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;

public class CardProxyHelper {
    public static boolean isCreatingProxy = false;
    public static MethodHandler storedHandler;
    public static MethodFilter storedFilter;

    public static AbstractCard createSameInstanceProxy(AbstractCard card, MethodFilter filter, MethodHandler newBehaviour) {
        isCreatingProxy = true;
        storedHandler = newBehaviour;
        storedFilter = filter;
        return card.makeSameInstanceOf();
    }

    public static AbstractCard createStatEquivalentProxy(AbstractCard card, MethodFilter filter, MethodHandler newBehaviour) {
        isCreatingProxy = true;
        storedHandler = newBehaviour;
        storedFilter = filter;
        return card.makeStatEquivalentCopy();
    }
//        MethodHandler mi = new MethodHandler() {
//            public Object invoke(Object self, Method m, Method proceed,
//                                 Object[] args) throws Throwable {
//                System.out.println("Name: " + m.getName());
//                return proceed.invoke(self, args);  // execute the original method.
//            }
//        };

//     f.setFilter(new MethodFilter() {
//        public boolean isHandled(Method m) {
//            // ignore finalize()
//            return !m.getName().equals("finalize");
//        }
//    });
}
