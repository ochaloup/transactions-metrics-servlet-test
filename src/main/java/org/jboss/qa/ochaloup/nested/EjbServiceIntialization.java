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
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.sql.DataSource;

import org.jboss.qa.ochaloup.utils.Database;

@Singleton
@Startup
public class EjbServiceIntialization {
    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private DataSource ds;

    @PostConstruct
    public void postConstruct() {
        System.out.println("Post construct now: " + EjbServiceIntialization.class.getName());
        try {
            Database.selectCount(ds, EjbService.TABLE_NAME);
        } catch (Exception sqle) {
            Database.createTable(ds, EjbService.TABLE_NAME);
        }
        Database.doDelete(ds, EjbService.TABLE_NAME);
    }
}
