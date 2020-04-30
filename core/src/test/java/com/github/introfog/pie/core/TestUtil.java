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
package com.github.introfog.pie.core;

import com.github.introfog.pie.core.util.ShapePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

public final class TestUtil {
    private TestUtil() {
    }

    public static void comparingShapePairsList(List<ShapePair> cmpShapes, List<ShapePair> outShapes) {
        if (cmpShapes.size() != outShapes.size()) {
            Assert.assertEquals("Different number of shape collisions", cmpShapes.size(), outShapes.size());
        }

        Map<Integer, List<ShapePair>> cmpMap = new HashMap<>(cmpShapes.size());
        for (ShapePair pair : cmpShapes) {
            int hashCode = pair.hashCode();
            cmpMap.putIfAbsent(hashCode, new ArrayList<>());
            cmpMap.get(hashCode).add(pair);
        }

        Map<Integer, List<ShapePair>> outMap = new HashMap<>(cmpShapes.size());
        for (ShapePair pair : outShapes) {
            int hashCode = pair.hashCode();
            outMap.putIfAbsent(hashCode, new ArrayList<>());
            outMap.get(hashCode).add(pair);
        }

        if (cmpMap.size() != outMap.size()) {
            Assert.assertEquals("Different size of maps", cmpMap.size(), outMap.size());
        }

        for (Integer hash : cmpMap.keySet()) {
            List<ShapePair> cmpList = cmpMap.get(hash);
            List<ShapePair> outList = outMap.get(hash);

            Assert.assertNotNull("Out map does not contain hash " + hash + " from cmp map", outList);
            Assert.assertTrue("Values cmp and out map for hash " + hash + " are different", cmpList.containsAll(outList));
        }
    }
}
