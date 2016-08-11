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

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.jboss.qa.ochaloup.utils.Database;

@Stateless
public class Ejb1 {
    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private DataSource ds;

    @Resource(mappedName = "java:jboss/TransactionManager")
    protected TransactionManager tm;

    public void createA() {
        String value = "Ultimate answer";
        Database.doInsert(ds, EjbService.TABLE_NAME, EjbService.ID, value);
        System.out.println("Inserted value " + value + " to table " + EjbService.TABLE_NAME);

        try {
            System.out.println("Currently under transaction " + tm.getTransaction() + " in status " + tm.getStatus());
        } catch (Exception e) {
            System.out.println("Not possible to find out transaction and its state");
            e.printStackTrace();
        }
   }
}
