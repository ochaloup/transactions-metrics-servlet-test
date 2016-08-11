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

package org.jboss.qa.ochaloup.nested;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import org.jboss.qa.ochaloup.utils.Database;

@Stateless
public class EjbService {
    public static final String TABLE_NAME = "nested";
    public static final int ID = 1;

    @Inject
    private Ejb1 ejb1;

    @Inject
    private Ejb2 ejb2;

    @Inject
    private EjbService service;

    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private DataSource ds;

    @Resource(mappedName = "java:jboss/TransactionManager")
    protected TransactionManager tm;

    public void dowork(){
        ejb1.createA();
        ejb2.processA();

        System.out.println("Current value is: " + Database.doSelect(ds, TABLE_NAME, ID));
        getTxnInfo();

        service.checkValueInNewTxn();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void checkValueInNewTxn() {
        System.out.println("Value is new transaction: " + Database.doSelect(ds, TABLE_NAME, ID));
        getTxnInfo();
    }

    private void getTxnInfo() {
        try {
            System.out.println("Currently under transaction " + tm.getTransaction() + " in status " + tm.getStatus());
        } catch (Exception e) {
            System.out.println("Not possible to find out transaction and its state");
            e.printStackTrace();
        }
    }
}
