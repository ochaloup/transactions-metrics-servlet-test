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

package org.jboss.qa.ochaloup.utils;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class Database {
    private static final String ID_COLUMN_NAME = "id";
    private static final String VALUE_COLUMN_NAME = "a";

    private Database() {
        // just util class
    }

    public static void createTable(DataSource ds, String tableName) {
        try(Connection conn = ds.getConnection()) {
            conn.createStatement().executeUpdate(
                String.format("CREATE TABLE %s (%s INT, %s VARCHAR(255))", tableName, ID_COLUMN_NAME, VALUE_COLUMN_NAME));
        } catch (Exception sqle) {
            throw new RuntimeException("can't create table", sqle);
        }
    }

    public static int selectCount(DataSource ds, String tableName) {
        try(Connection conn = ds.getConnection()) {
            ResultSet resultset = conn.createStatement().executeQuery(String.format("SELECT count(1) FROM %s", tableName));
            if(resultset.next()) {
                return resultset.getInt(1);
            } else {
                return Integer.MIN_VALUE;
            }
        } catch (SQLException sqle) {
            throw new RuntimeException("select failed", sqle);
        }
    }

    public static void doInsert(DataSource ds, String tableName, int id, String value) {
        try(Connection conn = ds.getConnection()) {
            int updated = conn.createStatement().executeUpdate(
                    String.format("INSERT INTO %s (id, a) VALUES (%s,'%s')", tableName, id, value));
            System.out.println("INSERT to table " + tableName + " outcome: " + updated);
        } catch (SQLException sqle) {
            throw new RuntimeException("insert failed", sqle);
        }        
    }

    public static void doUpdate(DataSource ds, String tableName, int id, String value) {
        try(Connection conn = ds.getConnection()) {
            int updated = conn.createStatement().executeUpdate(
                String.format("UPDATE %s SET %s='%s' WHERE %s=%s", tableName, VALUE_COLUMN_NAME, value, ID_COLUMN_NAME, id));
            System.out.println("UPDATE to table " + tableName + " outcome: " + updated);
        } catch (SQLException sqle) {
            throw new RuntimeException("update failed", sqle);
        }        
    }

    public static void doDelete(DataSource ds, String tableName) {
        try(Connection conn = ds.getConnection()) {
            conn.createStatement().executeUpdate(String.format("DELETE FROM %s", tableName));
        } catch (SQLException sqle) {
            throw new RuntimeException("delete failed", sqle);
        }        
    }
    
    public static List<String> doSelect(DataSource ds, String tableName, int id) {
        try(Connection conn = ds.getConnection()) {
            ResultSet resultset = conn.createStatement().executeQuery(
                String.format("SELECT %s FROM %s WHERE %s = %s", VALUE_COLUMN_NAME, tableName, ID_COLUMN_NAME, id));
            List<String> result = new ArrayList<>();
            while(resultset.next()) {
                result.add(resultset.getString(1));
            }
            return result;
        } catch (SQLException sqle) {
            throw new RuntimeException("Select of id " + id + " in table " + tableName + " failed", sqle);
        }     
    }
}
