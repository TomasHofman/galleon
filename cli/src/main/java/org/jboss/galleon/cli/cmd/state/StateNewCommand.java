/*
 * Copyright 2016-2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.galleon.cli.cmd.state;

import org.aesh.command.CommandDefinition;
import org.jboss.galleon.cli.CommandExecutionException;
import org.jboss.galleon.cli.PmCommandInvocation;
import org.jboss.galleon.cli.PmSession;
import org.jboss.galleon.cli.PmSessionCommand;
import org.jboss.galleon.cli.model.state.State;

/**
 *
 * @author jdenise@redhat.com
 */
@CommandDefinition(name = "new", description = "New provisioning state", activator = NoStateCommandActivator.class)
public class StateNewCommand extends PmSessionCommand {

    @Override
    protected void runCommand(PmCommandInvocation invoc) throws CommandExecutionException {
        if (invoc.getPmSession().getState() != null) {
            throw new CommandExecutionException("Provisioning session already set");
        }
        State session;
        try {
            session = new State(invoc.getPmSession());
        } catch (Exception ex) {
            throw new CommandExecutionException(ex);
        }
        invoc.getPmSession().setState(session);
        invoc.setPrompt(PmSession.buildPrompt(invoc.getPmSession().getState().getPath()));
        invoc.println("Entering provisioning composition mode. Use 'feature-pack add' command to add content. Call 'leave' to leave this mode.");
    }

}
