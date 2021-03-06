/**
 * <p>
 * Copyright (c) 2008 The Regents of the University of California<br>
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

package org.dapper.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.dapper.server.flow.FlowBuilder;

/**
 * A subclass of {@link JDialog} for aiding in the creation of command-line arguments for {@link FlowBuilder}
 * instantiation.
 * 
 * @author Roy Liu
 */
@SuppressWarnings("serial")
public class FlowBuilderDialog extends JDialog {

    /**
     * Default constructor.
     */
    public FlowBuilderDialog(final CodeletTree tree, final Class<? extends FlowBuilder> clazz, final ClassLoader cl) {

        super((Frame) null, false);

        setTitle("Arguments");

        final String[] argumentNames = clazz.getAnnotation(Program.class).arguments();

        JPanel main = new JPanel();

        main.setLayout(new GridBagLayout());
        main.setBorder(null);
        main.setOpaque(true);
        main.setFocusable(false);
        main.setBackground(Color.white);
        main.setForeground(Color.black);

        GridBagConstraints c = new GridBagConstraints();

        final JTextField[] textFields = new JTextField[argumentNames.length];

        for (int i = 0, n = argumentNames.length; i < n; i++) {

            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.CENTER;
            c.gridx = 0;
            c.gridy = i;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.weightx = 0.5;
            c.weighty = 1.0 / (n + 1);

            main.add(new JLabel(argumentNames[i]), c);

            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.CENTER;
            c.gridx = 1;
            c.gridy = i;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.weightx = 0.5;
            c.weighty = 1.0 / (n + 1);

            main.add(textFields[i] = new JTextField(), c);
        }

        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = argumentNames.length;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0 / (argumentNames.length + 1);

        JButton dismissButton = new JButton("ok");

        dismissButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {

                String[] args = new String[argumentNames.length];

                for (int i = 0, n = argumentNames.length; i < n; i++) {
                    args[i] = textFields[i].getText();
                }

                tree.runFromCommands(clazz, cl, args);

                ((Frame) getParent()).dispose();
            }
        });

        main.add(dismissButton, c);

        //

        setContentPane(main);
        setSize(new Dimension(100 + (argumentNames.length * 100), 200));
        setLocationRelativeTo(null);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
