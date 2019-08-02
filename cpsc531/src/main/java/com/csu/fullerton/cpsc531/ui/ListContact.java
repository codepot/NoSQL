/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csu.fullerton.cpsc531.ui;

import com.csu.fullerton.cpsc531.database.Cassandra;
import com.csu.fullerton.cpsc531.obj.Contact;
import com.csu.fullerton.cpsc531.obj.Department;
import com.csu.fullerton.cpsc531.ui.custom.RoundJTextField;
import com.csu.fullerton.cpsc531.ui.custom.RoundedBorder;
import com.csu.fullerton.cpsc531.ui.utils.ImageFilter;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author Peter
 */
public class ListContact extends javax.swing.JFrame {

    private File workingDirectory = new File(System.getProperty("user.dir"));
    private File defaultProfilePhoto = new File(getClass().getResource("/images/profile.png").getPath());
    private File file = defaultProfilePhoto;
    protected Cassandra cassandra;
    private InsertContact insertContact;
    protected List<Department> departments = new ArrayList<>();
    DefaultListModel<Contact> contactModel = new DefaultListModel<Contact>();
    List<Contact> contacts = new ArrayList<Contact>();
    protected List<Contact> management = new ArrayList<>();
    Contact selectedContact = null;
    int index = -1;
    protected ImageIcon imgIcon_info;

    /**
     * Creates new form ListContact
     */
    public ListContact() {
        this.imgIcon_info = new ImageIcon(getClass().getResource("/images/info.png").getPath());
        initComponents();
        cassandra = new Cassandra();
        ImageIcon imgicon = new ImageIcon(getClass().getResource("/images/icon.png").getPath());
        setIconImage(imgicon.getImage());
        setProfilePhoto(defaultProfilePhoto);
        insertContact = new InsertContact(this);
        DecimalFormat df = new java.text.DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingSize(3);

        btn_addContact.setBorder(new RoundedBorder(4));

        txt_Salary.setValue(new Double(0.00));

        txt_Salary.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(df)));
        list_contact.setModel(contactModel);

        // Load all departments from the database
        loadDepartments();

        // Load all the contact from the database
        loadContacts(cassandra.selectAllContacts());
        list_contact.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.list_contact.setCellRenderer(getRenderer());

        setCenterOfTheScreen();

        // closing cassandra session and then the program
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to exit the program?", "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    cassandra.close();
                    System.exit(0);
                }
            }
        });
        txt_search.getCaret().setDot(5);
        _btn_delete.setEnabled(false);
        _btn_update.setEnabled(false);

    }

    // set the contact info the the listbox on User Interface
    protected void loadContacts(ResultSet listOfContacts) {

        contacts.clear();
        contactModel.removeAllElements();
        for (Row row : listOfContacts) {
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
            //c.setPhoto(row.getBytes(""));  

            c.setReport_to(row.getUUID("report_to"));
            c.setDepartment_code(row.getString("department_code"));
            contacts.add(c);
            contactModel.addElement(c);

        }
        sordByLastname();
    }

    // Sort the contact list by Lastname
    protected void sordByLastname() {
        java.util.Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact t, Contact t1) {
                Contact p1 = (Contact) t;
                Contact p2 = (Contact) t1;
                // if last names are the same compare first names
                if (p1.getLastname().equals(p2.getLastname())) {
                    return p1.getFirstname().compareTo(p2.getFirstname());
                }
                return p1.getLastname().compareTo(p2.getLastname());
            }
        });

        contactModel.removeAllElements();
        for (Contact c : contacts) {
            contactModel.addElement(c);
        }

    }

    // sorting contac list by firstname
    protected void sordByFirstname() {
        java.util.Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact t, Contact t1) {
                Contact p1 = (Contact) t;
                Contact p2 = (Contact) t1;
                // if last names are the same compare first names
                if (p1.getFirstname().equals(p2.getFirstname())) {
                    return p1.getLastname().compareTo(p2.getLastname());
                }
                return p1.getFirstname().compareTo(p2.getFirstname());
            }
        });

        contactModel.removeAllElements();
        for (Contact c : contacts) {
            contactModel.addElement(c);
        }

    }

    // load all the departments from the database
    private void loadDepartments() {
        departments = cassandra.selectAllDepartments();
        combo_department.removeAllItems();
        for (Department department : departments) {
            combo_department.addItem(department.toString());
        }
    }
    
    private int searchContactLocally(UUID contactID){
        int i = -1;
        for(Contact c : contacts){
            i++;
            if(c.getContactId().equals(contactID)){
                System.out.println("--->"+i);
                return i;
            }
        }
        return -1;
    }

    // load all the people who can be reported to
    private void loadManagement(UUID contactId) {
        combo_report_to.removeAllItems();
        ResultSet resultSet = cassandra.selectAllContacts();
        management.clear();

        for (Row row : resultSet) {
            Contact c = new Contact();
            String role = row.getString("role");
            c.setContactId(row.getUUID("id"));
            c.setFirstname(row.getString("firstname"));
            c.setLastname(row.getString("lastname"));
            c.setCompany(row.getString("company"));
            c.setAddress1(row.getString("address1"));
            c.setAddress2(row.getString("address2"));
            c.setEmail(row.getString("email"));
            c.setTelephone(row.getString("telephone"));
            c.setCellphone(row.getString("cellphone"));
            c.setRole(role);
            c.setSalary(row.getDouble("salary"));
            c.setReport_to(row.getUUID("report_to"));
            c.setDepartment_code(row.getString("department_code"));
            if (!c.getRole().equals("Employee") && !c.getRole().equals("Other") && !c.getContactId().equals(contactId)) {
                management.add(c);
            }
        }
        // Sorting manager-person list yby lastname
        java.util.Collections.sort(management, new Comparator<Contact>() {
            @Override
            public int compare(Contact t, Contact t1) {
                Contact p1 = (Contact) t;
                Contact p2 = (Contact) t1;
                // if last names are the same compare first names
                if (p1.getLastname().equals(p2.getLastname())) {
                    return p1.getFirstname().compareTo(p2.getFirstname());
                }
                return p1.getLastname().compareTo(p2.getLastname());
            }
        });

        // how the manager-person list display on combobox
        combo_report_to.addItem("None");
        for (Contact c : management) {
            System.out.println("ROLE: " + c.getRole());
            combo_report_to.addItem(StringUtils.rightPad(c.getDepartment_code(), 6) + c.toString() + " - " + c.getRole());

        }
    }

    // read profile photo from file
    private void setProfilePhoto(File file) {
        ImageIcon icon;
        try {
            icon = new ImageIcon(ImageIO.read(file));
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance(165, 165, java.awt.Image.SCALE_SMOOTH);
            _btn_photo.setText("");
            _btn_photo.setIcon(new ImageIcon(newimg));
        } catch (IOException ex) {
            Logger.getLogger(InsertContact.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        list_contact = new javax.swing.JList();
        txt_search = new RoundJTextField(15);
        checkBoxSortByFirstname = new javax.swing.JCheckBox();
        btn_addContact = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        _btn_photo = new javax.swing.JButton();
        _btn_update = new javax.swing.JButton();
        _btn_delete = new javax.swing.JButton();
        btn_removePhoto = new javax.swing.JButton();
        combo_department = new javax.swing.JComboBox<String>();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        combo_report_to = new javax.swing.JComboBox<String>();
        jLabel18 = new javax.swing.JLabel();
        combo_role = new javax.swing.JComboBox<String>();
        jLabel19 = new javax.swing.JLabel();
        _btn_search_byFirstname = new javax.swing.JButton();
        _btn_search_byLastname = new javax.swing.JButton();
        _btn_viewAllContacts = new javax.swing.JButton();
        txt_cellphone = new javax.swing.JFormattedTextField();
        txt_telephone = new javax.swing.JFormattedTextField();
        txt_firstname = new javax.swing.JFormattedTextField();
        txt_lastname = new javax.swing.JFormattedTextField();
        txt_company = new javax.swing.JFormattedTextField();
        txt_address1 = new javax.swing.JFormattedTextField();
        txt_address2 = new javax.swing.JFormattedTextField();
        txt_email = new javax.swing.JFormattedTextField();
        txt_Salary = new javax.swing.JFormattedTextField();
        _btn_search_department = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pee-wee Contact Management");
        setResizable(false);

        list_contact.setFont(new java.awt.Font("Monospaced", 1, 16)); // NOI18N
        list_contact.setForeground(new java.awt.Color(0, 102, 153));
        list_contact.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Albares, Cammy", "Amigon, Minna", "Butt, James", "Caldarera, Kiley", "Darakjy, Josephine", "Dilliard, Leota", "Flosi, Fletcher", "Foller, Donette", "Garufi, Meaghan", "Inouye, Veronika", "Maclead, Abel", "Marrier, Kris", "Morasca, Simona", "Nicka, Bette", "Paprocki, Lenna", "Poquette, Mattie", "Rim, Gladys", "Ruta, Graciela", "Tollner, Mitsue", "Venere, Art", "Whobrey, Yuki", "Wieser, Sage" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        list_contact.setFixedCellHeight(32);
        list_contact.setSelectionBackground(new java.awt.Color(204, 255, 153));
        list_contact.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                double_clicked(evt);
            }
        });
        jScrollPane1.setViewportView(list_contact);

        txt_search.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        txt_search.setForeground(new java.awt.Color(0, 102, 102));
        txt_search.setName("txt_search"); // NOI18N

        checkBoxSortByFirstname.setText("Sort by First Name");
        checkBoxSortByFirstname.setActionCommand("");
        checkBoxSortByFirstname.setName("checkbox_sortByNumber"); // NOI18N
        checkBoxSortByFirstname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxSortByFirstnameActionPerformed(evt);
            }
        });

        btn_addContact.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        btn_addContact.setForeground(new java.awt.Color(0, 153, 102));
        btn_addContact.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btn_addContact.setMnemonic('A');
        btn_addContact.setText("Add New Contact");
        btn_addContact.setToolTipText("press Alt-A to add new payee");
        btn_addContact.setAlignmentY(0.0F);
        btn_addContact.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btn_addContact.setIconTextGap(0);
        btn_addContact.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btn_addContact.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add_rollover.png"))); // NOI18N
        btn_addContact.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_addContactMouseEntered(evt);
            }
        });
        btn_addContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addContactActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("First Name:");

        jLabel13.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Email:");

        jLabel9.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Last Name:");

        jLabel14.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Cell Phone:");

        jLabel15.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Telephone:");

        jLabel10.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Company:");

        jLabel11.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Address 1:");

        jLabel12.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Address 2:");

        _btn_photo.setBackground(java.awt.SystemColor.activeCaption);
        _btn_photo.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        _btn_photo.setForeground(new java.awt.Color(0, 102, 51));
        _btn_photo.setMnemonic('r');
        _btn_photo.setText("+ Photo");
        _btn_photo.setToolTipText("Remove selected photo");
        _btn_photo.setActionCommand("");
        _btn_photo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        _btn_photo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btn_photoActionPerformed(evt);
            }
        });

        _btn_update.setBackground(java.awt.SystemColor.activeCaption);
        _btn_update.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        _btn_update.setForeground(new java.awt.Color(0, 102, 51));
        _btn_update.setMnemonic('U');
        _btn_update.setText("Update");
        _btn_update.setMargin(new java.awt.Insets(0, 0, 0, 0));
        _btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btn_updateActionPerformed(evt);
            }
        });

        _btn_delete.setBackground(java.awt.SystemColor.activeCaption);
        _btn_delete.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        _btn_delete.setForeground(new java.awt.Color(0, 102, 51));
        _btn_delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/trash.png"))); // NOI18N
        _btn_delete.setMnemonic('D');
        _btn_delete.setText("Delete");
        _btn_delete.setMargin(new java.awt.Insets(0, 0, 0, 0));
        _btn_delete.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/images/trash_roll.png"))); // NOI18N
        _btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btn_deleteActionPerformed(evt);
            }
        });

        btn_removePhoto.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        btn_removePhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus.png"))); // NOI18N
        btn_removePhoto.setMnemonic('A');
        btn_removePhoto.setToolTipText("Remove photo");
        btn_removePhoto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_removePhoto.setIconTextGap(0);
        btn_removePhoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_removePhotoMouseEntered(evt);
            }
        });
        btn_removePhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removePhotoActionPerformed(evt);
            }
        });

        combo_department.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        combo_department.setForeground(new java.awt.Color(0, 153, 51));

        jLabel16.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Department:");

        jLabel17.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Reports to:");

        combo_report_to.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        combo_report_to.setForeground(new java.awt.Color(0, 153, 51));
        combo_report_to.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel18.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Role:");

        combo_role.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        combo_role.setForeground(new java.awt.Color(0, 153, 51));
        combo_role.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Employee", "Manager", "Supervisor", "CEO", "CFO", "Other" }));

        jLabel19.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Salary:");

        _btn_search_byFirstname.setBackground(java.awt.SystemColor.activeCaption);
        _btn_search_byFirstname.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        _btn_search_byFirstname.setForeground(new java.awt.Color(0, 102, 51));
        _btn_search_byFirstname.setMnemonic('U');
        _btn_search_byFirstname.setText("Search by Firstname");
        _btn_search_byFirstname.setMargin(new java.awt.Insets(0, 0, 0, 0));
        _btn_search_byFirstname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btn_search_byFirstnameActionPerformed(evt);
            }
        });

        _btn_search_byLastname.setBackground(java.awt.SystemColor.activeCaption);
        _btn_search_byLastname.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        _btn_search_byLastname.setForeground(new java.awt.Color(0, 102, 51));
        _btn_search_byLastname.setMnemonic('U');
        _btn_search_byLastname.setText("Search by Lastname");
        _btn_search_byLastname.setMargin(new java.awt.Insets(0, 0, 0, 0));
        _btn_search_byLastname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btn_search_byLastnameActionPerformed(evt);
            }
        });

        _btn_viewAllContacts.setBackground(java.awt.SystemColor.activeCaption);
        _btn_viewAllContacts.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        _btn_viewAllContacts.setForeground(new java.awt.Color(0, 102, 51));
        _btn_viewAllContacts.setMnemonic('U');
        _btn_viewAllContacts.setText("List All Contact");
        _btn_viewAllContacts.setMargin(new java.awt.Insets(0, 0, 0, 0));
        _btn_viewAllContacts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btn_viewAllContactsActionPerformed(evt);
            }
        });

        txt_cellphone.setForeground(new java.awt.Color(0, 153, 51));
        txt_cellphone.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_cellphone.setToolTipText("XXX-XXX-XXXX");
        txt_cellphone.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        txt_telephone.setForeground(new java.awt.Color(0, 153, 51));
        txt_telephone.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_telephone.setToolTipText("XXX-XXX-XXXX");
        txt_telephone.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        txt_firstname.setForeground(new java.awt.Color(0, 153, 51));
        txt_firstname.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_firstname.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        txt_lastname.setForeground(new java.awt.Color(0, 153, 51));
        txt_lastname.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_lastname.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        txt_company.setForeground(new java.awt.Color(0, 153, 51));
        txt_company.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_company.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        txt_address1.setForeground(new java.awt.Color(0, 153, 51));
        txt_address1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_address1.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        txt_address2.setForeground(new java.awt.Color(0, 153, 51));
        txt_address2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_address2.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        txt_email.setForeground(new java.awt.Color(0, 153, 51));
        txt_email.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt_email.setToolTipText("example: johndoe@company.com");
        txt_email.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        txt_Salary.setForeground(new java.awt.Color(0, 153, 51));
        txt_Salary.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Salary.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_Salary.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N

        _btn_search_department.setBackground(java.awt.SystemColor.activeCaption);
        _btn_search_department.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        _btn_search_department.setForeground(new java.awt.Color(0, 102, 51));
        _btn_search_department.setMnemonic('U');
        _btn_search_department.setText("Search by Department");
        _btn_search_department.setMargin(new java.awt.Insets(0, 0, 0, 0));
        _btn_search_department.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _btn_search_departmentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxSortByFirstname)
                        .addGap(306, 306, 306)
                        .addComponent(_btn_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(_btn_update, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(331, 331, 331)
                        .addComponent(_btn_search_byFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(_btn_search_byLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(_btn_search_department, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(_btn_viewAllContacts, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                            .addComponent(txt_search)
                            .addComponent(btn_addContact, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(_btn_photo, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_removePhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_firstname, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_lastname, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_company, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_address1, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_address2, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_telephone, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_cellphone, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combo_department, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combo_report_to, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combo_role, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Salary, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel11, jLabel12, jLabel13, jLabel14, jLabel15, jLabel16, jLabel17, jLabel18, jLabel19, jLabel8, jLabel9});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {combo_department, combo_report_to, combo_role, txt_Salary, txt_address1, txt_address2, txt_cellphone, txt_company, txt_email, txt_firstname, txt_lastname, txt_telephone});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(_btn_search_byFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(_btn_viewAllContacts, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(_btn_search_byLastname, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(_btn_search_department, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 13, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(_btn_photo, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_removePhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txt_firstname, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txt_lastname, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txt_company, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txt_address1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txt_address2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txt_telephone, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txt_cellphone, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combo_department, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combo_report_to, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(combo_role, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(txt_Salary, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 5, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(checkBoxSortByFirstname))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(_btn_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(_btn_update, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_addContact, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {_btn_search_byFirstname, _btn_search_byLastname, _btn_search_department, _btn_viewAllContacts});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_addContactMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_addContactMouseEntered

    }//GEN-LAST:event_btn_addContactMouseEntered

    // add contact button's action event
    private void btn_addContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addContactActionPerformed
        setVisible(false);
        insertContact.loadManagement();
        insertContact.setCenterOfTheScreen();
        insertContact.setVisible(true);

    }//GEN-LAST:event_btn_addContactActionPerformed

    public void setCenterOfTheScreen() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2 - 10);
    }

    // choosing profile photo
    private void _btn_photoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__btn_photoActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(workingDirectory);
        fc.setFileFilter(new ImageFilter());
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        setProfilePhoto(file);
    }//GEN-LAST:event__btn_photoActionPerformed

    private void btn_removePhotoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_removePhotoMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_removePhotoMouseEntered

    // remove photo selected - default photo will be selected automatically
    private void btn_removePhotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removePhotoActionPerformed
        file = null;
        setProfilePhoto(defaultProfilePhoto);
    }//GEN-LAST:event_btn_removePhotoActionPerformed

    // sortByFirstName checkbox's action event
    private void checkBoxSortByFirstnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxSortByFirstnameActionPerformed
        if (checkBoxSortByFirstname.isSelected()) {
            sordByFirstname();
        } else {
            sordByLastname();
        }
    }//GEN-LAST:event_checkBoxSortByFirstnameActionPerformed

    // action when a contact item is double clicked
    private void double_clicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_double_clicked
        JList list = (JList) evt.getSource();
        if (evt.getClickCount() == 2) {
            index = list.getSelectedIndex();
            if (index >= 0) {
                loadContact();
                _btn_delete.setEnabled(true);
                _btn_update.setEnabled(true);
            }
        }
    }//GEN-LAST:event_double_clicked

    // Read info of a contact selected
    private void loadContact() {
        Contact c = cassandra.selectContactById(contacts.get(index).getContactId());
        selectedContact = c;
        if (c.getPhoto() != null) {// Load Photo
            File f = new File(workingDirectory + "/samplePhoto/temp.png");
            FileChannel wChannel;
            try {
                wChannel = new FileOutputStream(f, false).getChannel();
                wChannel.write(c.getPhoto());
                setProfilePhoto(f);
                file = f;
                setContact(c);
                wChannel.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ListContact.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ListContact.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            setProfilePhoto(defaultProfilePhoto);
        }
    }

    private void _btn_viewAllContactsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__btn_viewAllContactsActionPerformed
        if (checkBoxSortByFirstname.isSelected()) {
            checkBoxSortByFirstname.setSelected(false);
        }
        loadContacts(cassandra.selectAllContacts());
        reset();

    }//GEN-LAST:event__btn_viewAllContactsActionPerformed

    // Search by Contact's firstname action
    private void _btn_search_byFirstnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__btn_search_byFirstnameActionPerformed
        String keyword = txt_search.getText().trim();
        if (keyword.length() > 0) {
            loadContacts(cassandra.search(keyword, "firstname"));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please enter keyword.",
                    "Message",
                    JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(getClass().getResource("/images/info.png").getPath()));
            txt_search.requestFocus();
        }
        reset();
    }//GEN-LAST:event__btn_search_byFirstnameActionPerformed

    // Search by Contact's lastname action
    private void _btn_search_byLastnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__btn_search_byLastnameActionPerformed
        String keyword = txt_search.getText().trim();
        if (keyword.length() > 0) {
            loadContacts(cassandra.search(keyword, "lastname"));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please enter keyword.",
                    "Message",
                    JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(getClass().getResource("/images/info.png").getPath()));
            txt_search.requestFocus();
        }
        reset();
    }//GEN-LAST:event__btn_search_byLastnameActionPerformed

    // DELETE THE CONTACT
    private void _btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__btn_deleteActionPerformed
        int index = list_contact.getSelectedIndex();
        if (index >= 0) {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to delete the contact?", "Warning", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {

                cassandra.deleteContact(contacts.get(index).getContactId());

                if (checkBoxSortByFirstname.isSelected()) {
                    checkBoxSortByFirstname.setSelected(false);
                }
                // RELOAD ALL CONTACTS
                loadContacts(cassandra.selectAllContacts());
                reset();

                list_contact.setSelectedIndex(-1); // no contact is selected
                JOptionPane.showMessageDialog(this,
                        "Contact was deleted",
                        "Message",
                        JOptionPane.INFORMATION_MESSAGE,
                        new ImageIcon(getClass().getResource("/images/info.png").getPath()));
            }
        }
    }//GEN-LAST:event__btn_deleteActionPerformed

    private void _btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__btn_updateActionPerformed
        index = list_contact.getSelectedIndex();
        if (index >= 0) {

            String validateMsg = validateInput();
            if (validateMsg.length() == 0) {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to update the contact?", "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {

                    // READ CONTACT FROM THE FORM
                    Contact newContact = getContact();
                    newContact.setContactId(selectedContact.getContactId());
                    // NATIVE SQL USED
                    // cassandra.updateContact(newContact);

                    // UPDATE CONTACT USING QueryBuilder
                    cassandra.updateContact(newContact);

                    // RELOAD ALL THE CONTACT AFTER UPDATING
                    loadContacts(cassandra.selectAllContacts());
                    //reset();

                    // READ INFO OF THE UPDATED CONTACT
                    //list_contact.setSelectedIndex(index);
                    // RELOAD CONTACTS
                    index = searchContactLocally(selectedContact.getContactId());
                    list_contact.setSelectedIndex(index);
                    list_contact.ensureIndexIsVisible(list_contact.getSelectedIndex());
                    loadContact();
                    JOptionPane.showMessageDialog(this,
                            "Contact was updated",
                            "Message",
                            JOptionPane.INFORMATION_MESSAGE,
                            imgIcon_info);
                    
                    //list_contact.setSelectedIndex(searchContactLocally(newContact.getContactId()));
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        validateMsg,
                        "Message",
                        JOptionPane.INFORMATION_MESSAGE,
                        imgIcon_info);
            }

        }
        // READ INFO OF THE UPDATED CONTACT
        
        //reset();
    }//GEN-LAST:event__btn_updateActionPerformed

    private String validateInput() {
        String message = "";
        if (txt_firstname.getText().trim().length() <= 0) {
            message += "First Name is required. \n";
        }
        if (txt_lastname.getText().trim().length() <= 0) {
            message += "Last Name is required. \n";
        }
        String email = txt_email.getText().trim();
        if (email.length() > 0 && !Validator.isEmail(email)) {
            message += "Email is invalid. \n";
        }

        String phone = txt_telephone.getText().trim();
        if (phone.length() > 0 && !Validator.isPhone(phone)) {
            message += "Telephone number is invalid. \n";
        }

        phone = txt_cellphone.getText().trim();
        if (phone.length() > 0 && !Validator.isPhone(phone)) {
            message += "Cell phone number is invalid. \n";
        }

        return message;
    }

    // SEARCH CONTACT BY DEPT_CODE
    private void _btn_search_departmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__btn_search_departmentActionPerformed
        String keyword = txt_search.getText().trim();
        if (keyword.length() > 0) {
            loadContacts(cassandra.search(keyword, "department_code"));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please enter keyword.",
                    "Message",
                    JOptionPane.INFORMATION_MESSAGE,
                    imgIcon_info);
            txt_search.requestFocus();
        }
        reset();
    }//GEN-LAST:event__btn_search_departmentActionPerformed

    // Create an instance of a Contact from the Form
    private Contact getContact() {
        String first = txt_firstname.getText();
        String last = txt_lastname.getText();
        String company = txt_company.getText();
        String address1 = txt_address1.getText();
        String address2 = txt_address2.getText();
        String email = txt_email.getText();
        String telephone = txt_telephone.getText();
        String cellphone = txt_cellphone.getText();
        String role = combo_role.getSelectedItem().toString();
        String department = combo_department.getSelectedItem().toString();

        Contact contact = new Contact(first, last, company, address1, address2, email, telephone, cellphone, role);
        contact.setDepartment_code(department);
        int reportTo = combo_report_to.getSelectedIndex();
        if (reportTo > 0) {
            contact.setReport_to(management.get(reportTo - 1).getContactId());
        }
        contact.setSalary(NumberUtils.toDouble(txt_Salary.getValue().toString()));

        // Get the profile photo from JFileChooser
        try {
            InputStream fis;
            if (file != null) {
                fis = new FileInputStream(file);
            } else {
                fis = new FileInputStream(defaultProfilePhoto);
            }
            byte[] bytes = new byte[fis.available() + 1];
            fis.read(bytes);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            contact.setPhoto(buffer);
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InsertContact.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InsertContact.class.getName()).log(Level.SEVERE, null, ex);
        }

        return contact;
    }

    private Contact getContact2() {
        String first = txt_firstname.getText();
        String last = txt_lastname.getText();
        String company = txt_company.getText();
        String address1 = txt_address1.getText();
        String address2 = txt_address2.getText();
        String email = txt_email.getText();
        String telephone = txt_telephone.getText();
        String cellphone = txt_cellphone.getText();
        String role = combo_role.getSelectedItem().toString();

        String department = combo_department.getSelectedItem().toString();

        Contact contact = new Contact(first, last, company, address1, address2, email, telephone, cellphone, role);
        contact.setSalary(NumberUtils.toDouble(txt_Salary.getValue().toString()));

        int reportTo = combo_report_to.getSelectedIndex();
        if (reportTo > 0) {
            contact.setReport_to(management.get(reportTo - 1).getContactId());
        }
        try {
            InputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available() + 1];

            fis.read(bytes);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            contact.setPhoto(buffer);
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InsertContact.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(InsertContact.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        contact.setDepartment_code(department);
        //contact.setReport_to(report_to);
        return contact;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ListContact.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListContact.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListContact.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListContact.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListContact().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton _btn_delete;
    private javax.swing.JButton _btn_photo;
    private javax.swing.JButton _btn_search_byFirstname;
    private javax.swing.JButton _btn_search_byLastname;
    private javax.swing.JButton _btn_search_department;
    private javax.swing.JButton _btn_update;
    private javax.swing.JButton _btn_viewAllContacts;
    private javax.swing.JButton btn_addContact;
    private javax.swing.JButton btn_removePhoto;
    private javax.swing.JCheckBox checkBoxSortByFirstname;
    private javax.swing.JComboBox<String> combo_department;
    private javax.swing.JComboBox<String> combo_report_to;
    private javax.swing.JComboBox<String> combo_role;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list_contact;
    private javax.swing.JFormattedTextField txt_Salary;
    private javax.swing.JFormattedTextField txt_address1;
    private javax.swing.JFormattedTextField txt_address2;
    private javax.swing.JFormattedTextField txt_cellphone;
    private javax.swing.JFormattedTextField txt_company;
    private javax.swing.JFormattedTextField txt_email;
    private javax.swing.JFormattedTextField txt_firstname;
    private javax.swing.JFormattedTextField txt_lastname;
    private javax.swing.JTextField txt_search;
    private javax.swing.JFormattedTextField txt_telephone;
    // End of variables declaration//GEN-END:variables

    private void setContact(Contact c) {
        txt_firstname.setText(c.getFirstname());
        txt_lastname.setText(c.getLastname());
        txt_company.setText(c.getCompany());
        txt_address1.setText(c.getAddress1());
        txt_address2.setText(c.getAddress2());
        txt_email.setText(c.getEmail());
        txt_telephone.setText(c.getTelephone());
        txt_cellphone.setText(c.getCellphone());
        combo_role.setSelectedItem(c.getRole());
        combo_department.setSelectedItem(c.getDepartment_code());
        txt_Salary.setValue(new Double(c.getSalary()));
        loadManagement(c.getContactId());
        combo_report_to.setSelectedItem(cassandra.getReport_to(c.getReport_to()));
        //combo_report_to.setSelectedIndex(0);
    }

    private void reset() {
        txt_search.setText("");
        txt_firstname.setText("");
        txt_lastname.setText("");
        txt_company.setText("");
        txt_address1.setText("");
        txt_address2.setText("");
        txt_email.setText("");
        txt_telephone.setText("");
        txt_cellphone.setText("");
        combo_role.setSelectedIndex(0);
        combo_department.setSelectedIndex(0);
        combo_report_to.setSelectedIndex(0);
        txt_Salary.setValue(new Double(0.00));
        file = null;
        selectedContact = null;
        setProfilePhoto(defaultProfilePhoto);
        _btn_delete.setEnabled(false);
        _btn_update.setEnabled(false);
        txt_search.requestFocus();
    }

    private ListCellRenderer<? super String> getRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                return listCellRendererComponent;
            }
        };
    }

}
