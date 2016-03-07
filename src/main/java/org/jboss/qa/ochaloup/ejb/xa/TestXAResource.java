/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.qa.ochaloup.ejb.xa;

import java.util.logging.Logger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class TestXAResource implements XAResource {
    private static final Logger log = Logger.getLogger(TestXAResource.class.getName());
    private Do whatToDo;
    
    public enum Do {
        CLEAN, COMMIT_RMFAIL;
    }

    public TestXAResource() {
        this.whatToDo = Do.CLEAN;
    }

    public TestXAResource(Do whatToDo) {
        this.whatToDo = whatToDo;
    }

    @Override
    public void commit(Xid arg0, boolean arg1) throws XAException {
        log.info("commit xid " + arg0);
        if(whatToDo == Do.COMMIT_RMFAIL) {
            throw new XAException(XAException.XAER_RMFAIL);
        }
    }

    @Override
    public void end(Xid arg0, int arg1) throws XAException {
        log.info("end xid " + arg0);
    }

    @Override
    public void forget(Xid arg0) throws XAException {
        log.info("forget xid " + arg0);
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    @Override
    public boolean isSameRM(XAResource arg0) throws XAException {
        return false;
    }

    @Override
    public int prepare(Xid arg0) throws XAException {
        log.info("prepare xid " + arg0);
        return XAResource.XA_OK;
    }

    @Override
    public Xid[] recover(int arg0) throws XAException {
        return new Xid[]{};
    }

    @Override
    public void rollback(Xid arg0) throws XAException {
        log.info("rollback xid " + arg0);
    }

    @Override
    public boolean setTransactionTimeout(int arg0) throws XAException {
        return true;
    }

    @Override
    public void start(Xid arg0, int arg1) throws XAException {
        log.info("start xid " + arg0);
    }

}
