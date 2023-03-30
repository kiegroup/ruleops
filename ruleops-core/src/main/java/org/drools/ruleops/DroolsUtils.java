package org.drools.ruleops;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class DroolsUtils {
    private DroolsUtils() {
        // only static
    }

    public static Collection<?> notFoundMap(Map<?, ?> left, Map<?, ?> right) {
        Collection<?> result = new HashSet<>(left.entrySet());
        result.removeAll(right.entrySet());
        return result;
    }
}
