import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnrollWindow extends JFrame {
    private Connection connection;
    private MainWindow main;
    public EnrollWindow(MainWindow main,Connection connection) {
        this.main = main;
        this.connection = connection;
        JPanel contentPane = new JPanel(new BorderLayout(0,0));
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                main.setEnabled(true);
            }
        });

        JPanel section2 = new JPanel(new GridLayout(3,2,20,20));
        JLabel welcomeMessage = new JLabel("Εγγραφή νέου καθηγητή");
        JLabel firstNameLabel = new JLabel("Όνομα: ");
        JTextField firstNameField = new JTextField(15);
        JLabel lastNameLabel = new JLabel("Επώνυμο: ");
        JTextField lastNameField = new JTextField(15);
        JLabel availableLabel = new JLabel("Διαθεσιμότητα: ");
        JTextField availableField = new JTextField(15);
        JPanel section3 = new JPanel(new FlowLayout(FlowLayout.CENTER,0,20));
        JButton insertButton = new JButton("Εγγραφή");
        this.setTitle("Φόρμα Εισαγωγής");
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane.setPreferredSize(new Dimension(480,300));
        contentPane.setBackground(new Color(0xF7E3CC));

        welcomeMessage.setPreferredSize(new Dimension(400,80));
        welcomeMessage.setHorizontalAlignment(JLabel.CENTER);
        welcomeMessage.setVerticalAlignment(JLabel.CENTER);
        welcomeMessage.setFont(new Font("Consolas",Font.BOLD,20));

        firstNameLabel.setFont(new Font("Consolas",Font.BOLD,16));
        firstNameField.setFont(new Font("Consolas",Font.PLAIN,16));
        lastNameLabel.setFont(new Font("Consolas",Font.BOLD,16));
        lastNameField.setFont(new Font("Consolas",Font.PLAIN,16));
        availableLabel.setFont(new Font("Consolas",Font.BOLD,16));
        availableField.setFont(new Font("Consolas",Font.PLAIN,16));

        section2.setBackground(new Color(0xF7E3CC));
        section2.add(firstNameLabel);
        section2.add(firstNameField);
        section2.add(lastNameLabel);
        section2.add(lastNameField);
        section2.add(availableLabel);
        section2.add(availableField);
        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER));
        container.setBackground(new Color(0xF7E3CC));
        container.add(section2);

        section3.setBackground(new Color(0xF7E3CC));
        insertButton.setMaximumSize(new Dimension(60,20));
        insertButton.setFocusable(false);
        section3.add(insertButton);

        insertButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String availability = availableField.getText().trim();
            if (!main.checkData(firstName, lastName, availability)) return;
            if (checkIfTeacherExists(firstName,lastName)) {
                JOptionPane.showMessageDialog(this,"Ο καθηγητής υπάρχει ήδη",
                        "Προειδοποίηση",JOptionPane.ERROR_MESSAGE);
            } else {
                addTeacher(firstName,lastName,availability);
                main.setEnabled(true);
                this.dispose();
            }
        });

        contentPane.add(section3,BorderLayout.SOUTH);
        contentPane.add(container,BorderLayout.CENTER);
        contentPane.add(welcomeMessage,BorderLayout.NORTH);
        this.add(contentPane);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    private boolean checkIfTeacherExists(String firstName, String lastName) {
        String findTeacher = "SELECT * FROM teachers WHERE LOWER(firstname) = LOWER(?) " +
                "AND LOWER(lastname) = LOWER(?)";
        try(PreparedStatement psFind = connection.prepareStatement(findTeacher)) {
            psFind.setString(1,firstName);
            psFind.setString(2,lastName);
            ResultSet rs = psFind.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Η αναζήτηση καθηγητή απέτυχε!",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void addTeacher(String firstName, String lastName , String availability) {
        String insertTeacher = "INSERT INTO teachers (firstname,lastname,available) VALUES (?,?,?)";
        try (PreparedStatement psTeacher = connection.prepareStatement(insertTeacher)) {
            psTeacher.setString(1,
                    firstName.substring(0,1).toUpperCase()+firstName.substring(1).toLowerCase());
            psTeacher.setString(2,
                    lastName.substring(0,1).toUpperCase()+lastName.substring(1).toLowerCase());
            int availabilityInt = Integer.parseInt(availability);
            psTeacher.setInt(3,availabilityInt);
            psTeacher.executeUpdate();
            JOptionPane.showMessageDialog(this,"Επιτυχημένη εγγραφή",
                    "Επιτυχία",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Η εγγραφή νέου καθηγητή απέτυχε!",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
        }
    }
}
