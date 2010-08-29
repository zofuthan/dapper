/**
 * <p>
 * Copyright (C) 2008 The Regents of the University of California<br />
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

package ex;

import static dapper.Constants.LOCAL;

import java.util.List;

import dapper.server.flow.DummyEdge;
import dapper.server.flow.Flow;
import dapper.server.flow.FlowBuilder;
import dapper.server.flow.FlowEdge;
import dapper.server.flow.FlowNode;
import dapper.server.flow.HandleEdge;
import dapper.server.flow.StreamEdge;
import dapper.ui.Program;

/**
 * A demonstration of complex Dapper functionality.
 * 
 * @author Roy Liu
 */
@Program
public class ComplexTest implements FlowBuilder {

    /**
     * Default constructor.
     */
    public ComplexTest(String[] args) {
    }

    @Override
    public void build(Flow flow, //
            List<FlowEdge> inEdges, //
            List<FlowNode> outNodes) {

        FlowNode dn = new FlowNode("ex.Debug") //
                .setDomainPattern(LOCAL);
        FlowNode cn = new FlowNode("ex.Create") //
                .setDomainPattern(LOCAL);

        FlowNode a = dn.clone();
        FlowNode b = cn.clone();
        FlowNode c = new FlowNode("ex.FanSubflow") //
                .setDomainPattern(LOCAL);
        FlowNode d = dn.clone();
        FlowNode e = dn.clone();
        FlowNode f = dn.clone();
        FlowNode g = new FlowNode("ex.Error") //
                .setDomainPattern(LOCAL);

        flow.add(a);
        flow.add(b);
        flow.add(e, new HandleEdge(a, e));
        flow.add(c, new HandleEdge(b, c).setExpandOnEmbed(true), new StreamEdge(a, c));
        flow.add(d, new DummyEdge(c, d));
        flow.add(f, new HandleEdge(d, f));
        flow.add(g);
        flow.add(new StreamEdge(e, g).setInverted(true));
        flow.add(new StreamEdge(f, g).setInverted(true));
    }

    /**
     * Gets a human-readable description.
     */
    @Override
    public String toString() {
        return "Complex Test";
    }
}
