/**
 * <p>
 * Copyright (c) 2010 The Regents of the University of California<br>
 * All rights reserved.
 * </p>
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * </p>
 * <ul>
 * <li>Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.</li>
 * <li>Neither the name of the author nor the names of any contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.</li>
 * </ul>
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </p>
 */

package org.dapper.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.shared.util.Control;

import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * An implementation of {@link MatchingAlgorithm} that uses the <a
 * href="http://en.wikipedia.org/wiki/Edmonds%E2%80%93Karp_algorithm">Edmonds-Karp maximum flow</a> algorithm
 * underneath, as provided by the <a href="http://jung.sourceforge.net/">JUNG</a> project's {@link EdmondsKarpMaxFlow}
 * class.
 * 
 * @author Roy Liu
 */
public class MaximumFlowMatching implements MatchingAlgorithm {

    /**
     * Default constructor.
     */
    public MaximumFlowMatching() {
    }

    @Override
    public <R extends Requirement<S>, S> Map<R, S> match(Collection<R> requirements, Collection<S> satisfiers) {

        List<R> rList = new ArrayList<R>();
        List<R> rListTrivial = new ArrayList<R>();

        // Consider only nontrivial requirements for matching.
        for (R requirement : requirements) {
            (!requirement.isTrivial() ? rList : rListTrivial).add(requirement);
        }

        List<S> sList = new ArrayList<S>(satisfiers);

        int nr = rList.size();
        int ns = sList.size();

        DirectedGraph<Integer, Integer> dag = new DirectedSparseGraph<Integer, Integer>();

        // Add vertices and edges.

        for (int i = 0, n = nr + ns; i < n; i++) {
            dag.addVertex(i);
        }

        int ie = 0;

        for (int ir = 0; ir < nr; ir++) {

            for (int is = 0; is < ns; is++) {

                if (rList.get(ir).isSatisfied(sList.get(is))) {
                    dag.addEdge(ie++, ir, is + nr);
                }
            }
        }

        int ne = ie;

        // Add the source, sink, and associated edges.

        dag.addVertex(nr + ns);
        dag.addVertex(nr + ns + 1);

        for (int ir = 0; ir < nr; ir++) {
            dag.addEdge(ie++, nr + ns, ir);
        }

        for (int is = 0; is < ns; is++) {
            dag.addEdge(ie++, is + nr, nr + ns + 1);
        }

        // Compute the maximum flow.

        Map<Integer, Number> edgeFlowMap = new HashMap<Integer, Number>();

        final int edgeFactoryOffset = ie;

        new EdmondsKarpMaxFlow<Integer, Integer>(dag, nr + ns, nr + ns + 1, //
                //
                new Transformer<Integer, Number>() {

                    @Override
                    public Number transform(Integer input) {
                        return 1;
                    }
                }, //
                edgeFlowMap, //
                //
                new Factory<Integer>() {

                    int ie = edgeFactoryOffset;

                    @Override
                    public Integer create() {
                        return this.ie++;
                    }
                }

        ).evaluate();

        // Match requirements with satisfiers.

        Set<S> sRemaining = new HashSet<S>(sList);
        Map<R, S> res = new HashMap<R, S>();

        for (ie = 0; ie < ne; ie++) {

            if ((Integer) edgeFlowMap.get(ie) > 0) {

                R r = rList.get(dag.getSource(ie));
                S s = sList.get(dag.getDest(ie) - nr);

                sRemaining.remove(s);
                Control.checkTrue(res.put(r, s) == null, //
                        "Requirements must be unique under object equality");
            }
        }

        Iterator<R> rItr = rListTrivial.iterator();
        Iterator<S> sItr = sRemaining.iterator();

        for (int i = 0, n = Math.min(rListTrivial.size(), sRemaining.size()); i < n; i++) {
            Control.checkTrue(res.put(rItr.next(), sItr.next()) == null, //
                    "Requirements must be unique under object equality");
        }

        return res;
    }
}
