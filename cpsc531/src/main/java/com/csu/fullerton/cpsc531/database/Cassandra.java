/**
 * @author Liam
 */
package com.csu.fullerton.cpsc531.database;

import com.csu.fullerton.cpsc531.obj.Contact;
import com.csu.fullerton.cpsc531.obj.Department;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
//import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

import java.util.ArrayList;

import java.util.List;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;

public class Cassandra {

    private static Cluster cluster;
    private static Session session;

    public Cassandra() {
        cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        session = cluster.connect();
        session.execute("USE contact_mgmt");
    }

    public void close() {
        session.close();
        cluster.close();
    }

    public static Cluster connect(String node) {
        /*returns cluster instance using contactpoint 
        which uses the address of the node that cassandra uses to connect */
        return Cluster.builder().addContactPoint(node).build();
    }

    // Select all contacts from the database
    public ResultSet selectAllContacts() {
        //cluster = Cluster.builder().addContactPoint("127.0.0.1").build();        
        session = cluster.connect();
        session.execute("USE contact_mgmt");
        ResultSet results = session.execute("SELECT * FROM contact");
        return results;
    }

    // insert a contact into database
    public UUID insertContact(Contact contact) {
        String query = "Insert into Contact (id, firstname, lastname, company, address1, address2, email, telephone, cellphone,"
                + "role, salary, photo, report_to, department_code) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = session.prepare(query);
        BoundStatement boundStatement = new BoundStatement(stmt);
        java.util.UUID uuid = java.util.UUID.randomUUID();
        contact.setContactId(uuid);
        boundStatement.bind(uuid, contact.getFirstname(), contact.getLastname(), contact.getCompany(),
                contact.getAddress1(), contact.getAddress2(), contact.getEmail(), contact.getTelephone(), contact.getCellphone(),
                contact.getRole(), contact.getSalary(), contact.getPhoto(), contact.getReport_to(), contact.getDepartment_code()
        );
        session.execute(boundStatement);

        return contact.getContactId();
    }

    // Update a contact using native SQL
    public boolean updateContact(Contact contact) {
        String query = "Update contact set firstname=?, lastname=?, company=?, address1=?, address2=?, email=?, telephone=?, cellphone=?,"
                + "role=?, salary=?, photo=?, report_to=?, department_code=? where id=?";
        PreparedStatement stmt = session.prepare(query);
        BoundStatement boundStatement = new BoundStatement(stmt);

        boundStatement.bind(contact.getFirstname(), contact.getLastname(), contact.getCompany(),
                contact.getAddress1(), contact.getAddress2(), contact.getEmail(), contact.getTelephone(), contact.getCellphone(),
                contact.getRole(), contact.getSalary(), contact.getPhoto(), contact.getReport_to(), contact.getDepartment_code(), contact.getContactId()
        );
        session.execute(boundStatement);
        return true;
    }

    // Updata a contact using QueryBuilder
    public void updateContact2(Contact contact) {
        //update
        Statement stmt = QueryBuilder.update("Contact").with(QueryBuilder.set("firstname", contact.getFirstname()))
                .and(QueryBuilder.set("lastname", contact.getLastname()))
                .and(QueryBuilder.set("company", contact.getCompany()))
                .and(QueryBuilder.set("address1", contact.getAddress1()))
                .and(QueryBuilder.set("address2", contact.getAddress2()))
                .and(QueryBuilder.set("email", contact.getEmail()))
                .and(QueryBuilder.set("telephone", contact.getTelephone()))
                .and(QueryBuilder.set("cellphone", contact.getCellphone()))
                .and(QueryBuilder.set("role", contact.getRole()))
                .and(QueryBuilder.set("salary", contact.getSalary()))
                .and(QueryBuilder.set("photo", contact.getPhoto()))
                .and(QueryBuilder.set("report_to", contact.getReport_to()))
                .and(QueryBuilder.set("department_code", contact.getDepartment_code()))
                .where(QueryBuilder.eq("id", contact.getContactId()));
        session.execute(stmt);
    }

    // Delete a contact
    public void deleteContact(UUID contactId) {
        Statement query = QueryBuilder.delete().from("contact").where(eq("id", contactId));
        session.execute(query);
    }

    // select one contact by ID
    public Contact selectContactById(UUID contactId) {
        //session = cluster.connect();
        //session.execute("USE contact_mgmt");

        Statement query = QueryBuilder.select().from("Contact").where(eq("id", contactId));

        Row row = session.execute(query).one();

        Contact c = new Contact();

        c.setContactId(row.getUUID("id"));
        c.setFirstname(row.getString("firstname"));
        c.setLastname(row.getString("lastname"));
        c.setCompany(row.getString("company"));
        c.setAddress1(row.getString("address1"));
        c.setAddress2(row.getString("address2"));
        c.setEmail(row.getString("email"));
        c.setTelephone(row.getString("telephone"));
        c.setCellphone(row.getString("cellphone"));
        c.setRole(row.getString("role"));
        c.setSalary(row.getDouble("salary"));
        c.setPhoto(row.getBytes("photo"));
        c.setReport_to(row.getUUID("report_to"));
        c.setDepartment_code(row.getString("department_code"));

        return c;
    }

    // Load all departments from the database
    public List<Department> selectAllDepartments() {
        List<Department> list = new ArrayList<>();
        ResultSet results = session.execute(QueryBuilder.select().all().from("Department"));
        //ResultSet results = session.execute("Select * from Department); // native SQL
        for (Row row : results) {
            list.add(new Department(row.getString("dept_code"), row.getString("dept_name")));
        }
        java.util.Collections.sort(list);

        return list;
    }

    // search USING 'LIKE' on firstname, lastname, or dept_code
    public ResultSet search(String keyword, String columnName) {
        String query = "Select * from Contact where " + columnName + " LIKE '%" + keyword + "%'";
        ResultSet results = session.execute(query);
        return results;
    }

    // who does a contact report to
    public String getReport_to(UUID contactId) {
        if (contactId == null) {
            return "None";
        }
        Statement query = QueryBuilder.select().from("Contact").where(eq("id", contactId));
        Row row = session.execute(query).one();
        String result = StringUtils.rightPad(row.getString("department_code"), 6) + row.getString("lastname") + ", " + row.getString("firstname") + " - " + row.getString("role");

        return result;
    }

}
