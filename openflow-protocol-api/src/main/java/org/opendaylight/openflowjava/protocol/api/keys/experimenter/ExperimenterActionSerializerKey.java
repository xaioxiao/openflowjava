/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.keys.experimenter;

import org.opendaylight.openflowjava.protocol.api.keys.ActionSerializerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.action.rev130731.Experimenter;

/**
 * @author michal.polkorab
 */
public final class ExperimenterActionSerializerKey extends ActionSerializerKey<Experimenter> 
        implements ExperimenterSerializerKey {

    /**
     * @param msgVersion protocol wire version
     * @param experimenterId experimenter / vendor ID
     */
    public ExperimenterActionSerializerKey(short msgVersion, Long experimenterId) {
        super(msgVersion, Experimenter.class, experimenterId);
    }

}