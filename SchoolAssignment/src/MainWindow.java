import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    private static Connection connection;
    public MainWindow() {
        JPanel contentPane  = new JPanel(new BorderLayout(0,5));
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent event) {
                String url = "jdbc:mysql://localhost:3306/school";
                String username = "devuser";
                String password = "41225#abc";
                try {
                    connection = DriverManager.getConnection(url, username, password);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(contentPane,"Η σύνδεση απέτυχε!",
                            "Σφάλμα",JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });


        JLabel welcomeMessage = new JLabel("Καλωσήρθατε στην Εφαρμογή Γραμματειακής Υποστήριξης");
        JPanel section2 = new JPanel(new FlowLayout(FlowLayout.CENTER,0,20));
        JPanel section3 = new JPanel(new FlowLayout(FlowLayout.CENTER,10,20));
        JLabel insertProfessorLabel = new JLabel("Εγγραφή νέου καθηγητή");
        JLabel searchProfessorLabel = new JLabel("Αναζήτηση καθηγητών");
        JLabel lastNameLabel = new JLabel("Επώνυμο: ");
        JTextField lastNameField = new JTextField(15);
        JButton searchButton = new JButton ("Αναζήτηση");
        JButton insertButton = new JButton("Εγγραφή");
        this.setTitle("Φόρμα Υποδοχής");
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        contentPane.setPreferredSize(new Dimension(640,480));
        contentPane.setBackground(new Color(0xF7E3CC));

        welcomeMessage.setPreferredSize(new Dimension(500,80));
        welcomeMessage.setHorizontalAlignment(JLabel.CENTER);
        welcomeMessage.setVerticalAlignment(JLabel.CENTER);
        welcomeMessage.setFont(new Font("Consolas",Font.BOLD,20));

        insertProfessorLabel.setPreferredSize(new Dimension(640,100));
        insertProfessorLabel.setHorizontalAlignment(JLabel.CENTER);
        insertProfessorLabel.setFont(new Font("Consolas",Font.BOLD,18));
        insertButton.setMaximumSize(new Dimension(60,20));
        insertButton.setFocusable(false);

        searchProfessorLabel.setPreferredSize(new Dimension(640,100));
        searchProfessorLabel.setHorizontalAlignment(JLabel.CENTER);
        searchProfessorLabel.setFont(new Font("Consolas",Font.BOLD,18));
        lastNameLabel.setFont(new Font("Consolas",Font.BOLD,16));
        lastNameField.setFont(new Font("Consolas",Font.PLAIN,16));
        searchButton.setMaximumSize(new Dimension(60,20));
        searchButton.setFocusable(false);

        section2.setPreferredSize(new Dimension(640,200));
        section2.setBackground(new Color(0xF7E3CC));
        section2.add(insertProfessorLabel);
        section2.add(insertButton);

        section3.setPreferredSize(new Dimension(640,200));
        section3.setBackground(new Color(0xF7E3CC));
        section3.add(searchProfessorLabel);
        section3.add(lastNameLabel);
        section3.add(lastNameField);
        section3.add(searchButton);

        insertButton.addActionListener(e -> {
            this.setEnabled(false);
            new EnrollWindow(this,connection);
        });
        searchButton.addActionListener(e -> {
            List<Teacher> teachers = findTeachers(lastNameField.getText().trim());
            if (!teachers.isEmpty()) {
                this.setEnabled(false);
                new SearchResultsWindow(this,connection,teachers);
            }
        });
        contentPane.add(section3,BorderLayout.SOUTH);
        contentPane.add(section2,BorderLayout.CENTER);
        contentPane.add(welcomeMessage,BorderLayout.NORTH);
        this.add(contentPane);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private List<Teacher> findTeachers(String lastName) {
        List<Teacher> teachers = new ArrayList<>();
        String searchTeacherByLastName ="SELECT * FROM teachers WHERE lastname LIKE ?";
        try (PreparedStatement psSearch = connection.prepareStatement(searchTeacherByLastName)) {
            psSearch.setString(1,"%"+ lastName +"%");
            ResultSet rs = psSearch.executeQuery();
            while (rs.next()) {
                teachers.add(new Teacher(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4)));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Ανεπιτυχής αναζήτηση στην βάση δεδομένων",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        if (teachers.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Δεν βρέθηκαν καθηγητές",
                    "Ενημέρωση",JOptionPane.INFORMATION_MESSAGE);
        }
        return teachers;
    }

    public boolean checkData (String firstName,String lastName,String availability) {
        if (firstName == null || firstName.isBlank()) {
            JOptionPane.showMessageDialog(this,"Το σύστημα δεν δέχεται κενό όνομα!",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (lastName == null || lastName.isBlank()) {
            JOptionPane.showMessageDialog(this,"Το σύστημα δεν δέχεται κενό επώνυμο!",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (availability != null && !availability.isBlank()) {
            try {
                int availabilityInt = Integer.parseInt(availability);
                if (availabilityInt <= 0 || availabilityInt > 16) {
                    JOptionPane.showMessageDialog(this,"Οι εργάσιμες ώρες είναι μεταξύ 1-16",
                            "Σφάλμα",JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Οι εργάσιμες ώρες πρέπει να είναι αριθμός!",
                        "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(this,"Το σύστημα δεν δέχεται κενό εργάσιμες ώρες!",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
