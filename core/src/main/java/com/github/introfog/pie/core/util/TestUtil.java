/*
    Copyright 2020 Dmitry Chubrick

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package com.github.introfog.pie.core.util;

import com.github.introfog.pie.core.shape.ShapePair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for comparing tests result. It is helper class for internal usage only.
 */
public final class TestUtil {
    private TestUtil() {
        // Empty constructor
    }

    /**
     * Checks that two set of the {@link ShapePair} are equals to each other.
     *
     * @param cmpShapePairs the expected set of the {@link ShapePair}
     * @param outShapePairs the actual set of the {@link ShapePair}
     * @param messagePrefix the message prefix
     * @return the string message with exception, if the sets are not equal, null otherwise
     */
    public static String assertEqualsShapePairsList(Set<ShapePair> cmpShapePairs, Set<ShapePair> outShapePairs, String messagePrefix) {
        if (cmpShapePairs.size() != outShapePairs.size()) {
            return messagePrefix + "Different number of shape collisions. "
                    + "Expected: " + cmpShapePairs.size() + "; actual: " + outShapePairs.size();
        }

        Map<Integer, Set<ShapePair>> cmpMap = TestUtil.getHashCodeMap(cmpShapePairs);

        Map<Integer, Set<ShapePair>> outMap = TestUtil.getHashCodeMap(outShapePairs);

        if (cmpMap.size() != outMap.size()) {
            return messagePrefix + "Different size of maps. Expected: " + cmpMap.size() + "; actual: " + outMap.size();
        }

        for (Integer hash : cmpMap.keySet()) {
            Set<ShapePair> cmpList = cmpMap.get(hash);
            Set<ShapePair> outList = outMap.get(hash);

            if (outList == null) {
                return messagePrefix + "Out map does not contain hash " + hash + " from cmp map.";
            }
            if (!cmpList.containsAll(outList)) {
                return messagePrefix + "Values cmp and out map for hash " + hash + " are different.";
            }
        }
        return null;
    }

    /**
     * Checks that two set of the {@link ShapePair} are equals to each other.
     *
     * @param cmpShapePairs the expected set of the {@link ShapePair}
     * @param outShapePairs the actual set of the {@link ShapePair}
     * @return the string message with exception, if the sets are not equal, null otherwise
     */
    public static String assertEqualsShapePairsList(Set<ShapePair> cmpShapePairs, Set<ShapePair> outShapePairs) {
        return TestUtil.assertEqualsShapePairsList(cmpShapePairs, outShapePairs, "");
    }

    private static Map<Integer, Set<ShapePair>> getHashCodeMap(Set<ShapePair> shapePairs) {
        Map<Integer, Set<ShapePair>> hashCodeMap = new HashMap<>(shapePairs.size());
        for (ShapePair pair : shapePairs) {
            int hashCode = pair.hashCode();
            hashCodeMap.putIfAbsent(hashCode, new HashSet<>());
            hashCodeMap.get(hashCode).add(pair);
        }
        return hashCodeMap;
    }
}
