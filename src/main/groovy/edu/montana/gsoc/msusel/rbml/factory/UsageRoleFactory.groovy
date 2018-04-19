/**
 * The MIT License (MIT)
 *
 * MSUSEL RBML DSL
 * Copyright (c) 2015-2018 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/**
 * 
 */
package edu.montana.gsoc.msusel.rbml.factory

import edu.montana.gsoc.msusel.rbml.PatternManager
import edu.montana.gsoc.msusel.rbml.events.DestTypeResolution
import edu.montana.gsoc.msusel.rbml.events.SourceTypeResolution
import edu.montana.gsoc.msusel.rbml.model.AtLeastOne
import edu.montana.gsoc.msusel.rbml.model.Multiplicity
import edu.montana.gsoc.msusel.rbml.model.SPS
import edu.montana.gsoc.msusel.rbml.model.Usage
/**
 * @author Isaac Griffith
 *
 */
class UsageRoleFactory extends AbstractFactory {

    /**
     * {@inheritDoc}
     */
    boolean isLeaf() {
        return false
    }

    /**
     * {@inheritDoc}
     */
    def newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
    throws InstantiationException, IllegalAccessException {
        Usage inst = new Usage()
        inst.setName(value)

        int lower = 1
        int upper = 1
        if (attributes) {
            if (attributes["mult"] != null) {
                String mult = attributes["mult"]
                mult = mult.replaceAll(/\*/, "-1")
                if (mult.contains("..")) {
                    lower = new Integer(mult.split(/\.\./)[0])
                    upper = new Integer(mult.split(/\.\./)[1])
                }
            }

            if (attributes["source"] != null) {
                PatternManager.instance.events << new SourceTypeResolution(role: inst, type: attributes["source"])
            }

            if (attributes["dest"] != null) {
                PatternManager.instance.events << new DestTypeResolution(role: inst, type: attributes["dest"])
            }
        }
        attributes.clear()
        inst.setMult(new Multiplicity(lower: lower, upper: upper))

        if (builder.parentNode != null && builder.parentNode instanceof SPS) {
            builder.parentNode.relations << inst
        } else if (builder.parentNode != null && builder.parentNode instanceof AtLeastOne) {
            builder.parentNode.relations << inst
        }

        inst
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent != null && parent instanceof AtLeastOne) {
            parent.relations << child
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object child) {
    }

}