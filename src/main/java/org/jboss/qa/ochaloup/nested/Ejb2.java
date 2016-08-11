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

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import org.jboss.qa.ochaloup.utils.Database;

@Stateless
public class Ejb2 {
    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private DataSource ds;

    @Resource(mappedName = "java:jboss/TransactionManager")
    protected TransactionManager tm;


    public void processA(){
        List<String> foundValueList = Database.doSelect(ds, EjbService.TABLE_NAME, EjbService.ID);

        String foundValue = foundValueList.iterator().next();
        String newValue = foundValue + " is 42";

        System.out.println("Updating value '" + foundValue + "' to value '" + newValue + "'");
        Database.doUpdate(ds, EjbService.TABLE_NAME, EjbService.ID, newValue);

        try {
            System.out.println("Currently under transaction " + tm.getTransaction() + " in status " + tm.getStatus());
        } catch (Exception e) {
            System.out.println("Not possible to find out transaction and its state");
            e.printStackTrace();
        }
   }
}
