import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsWindow extends JFrame {
    private Connection connection;
    private List<Teacher> teachers;
    private MainWindow main;
    private JTextField idField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField availableField;
    private int i = 0;
    public SearchResultsWindow(MainWindow main,Connection connection,List<Teacher> teachers) {
        this.main = main;
        this.connection = connection;
        this.teachers = teachers;
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                main.setEnabled(true); // ενεργοποίησε ξανά όταν κλείσει
            }
        });

        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        JPanel section2 = new JPanel(new GridLayout(4, 2, 20, 20));
        JLabel welcomeMessage = new JLabel("Αποτελέσματα αναζήτησης καθηγητών");
        JLabel idLabel = new JLabel("ID: ");
        idField = new JTextField(15);
        JLabel firstNameLabel = new JLabel("Όνομα: ");
        firstNameField = new JTextField(15);
        JLabel lastNameLabel = new JLabel("Επώνυμο: ");
        lastNameField = new JTextField(15);
        JLabel availableLabel = new JLabel("Διαθεσιμότητα: ");
        availableField = new JTextField(15);
        JPanel section3 = new JPanel(new GridLayout(4,1,0,20));
        JLabel navLabel = new JLabel("Πλοήγηση Αποτελεσμάτων");
        JPanel navButtons = new JPanel(new GridLayout(1,4,10,0));
        JButton first = new JButton("| <");
        JButton previous = new JButton("<");
        JButton next = new JButton(">");
        JButton last = new JButton("> |");
        JLabel actionsLabel = new JLabel("Ενέργειες");
        JPanel actionButtons = new JPanel(new GridLayout(1,3,10,0));
        JButton updateButton = new JButton("Ενημέρωση");
        JButton deleteButton = new JButton("Διαγραφή");
        JButton coursesButton = new JButton("Μαθήματα");

        this.setTitle("Φόρμα Αποτελεσμάτων");
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane.setPreferredSize(new Dimension(480, 420));
        contentPane.setBackground(new Color(0xF7E3CC));

        welcomeMessage.setPreferredSize(new Dimension(400, 80));
        welcomeMessage.setHorizontalAlignment(JLabel.CENTER);
        welcomeMessage.setVerticalAlignment(JLabel.CENTER);
        welcomeMessage.setFont(new Font("Consolas", Font.BOLD, 20));

        idLabel.setFont(new Font("Consolas",Font.BOLD,16));
        idField.setFont(new Font("Consolas",Font.PLAIN,16));
        idField.setEditable(false);
        idField.setFocusable(false);
        firstNameLabel.setFont(new Font("Consolas",Font.BOLD,16));
        firstNameField.setFont(new Font("Consolas",Font.PLAIN,16));
        lastNameLabel.setFont(new Font("Consolas",Font.BOLD,16));
        lastNameField.setFont(new Font("Consolas",Font.PLAIN,16));
        availableLabel.setFont(new Font("Consolas",Font.BOLD,16));
        availableField.setFont(new Font("Consolas",Font.PLAIN,16));

        navigateTeacher(i = 0);

        section2.setBackground(new Color(0xF7E3CC));
        section2.add(idLabel);
        section2.add(idField);
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
        navLabel.setFont(new Font("Consolas",Font.BOLD,16));
        actionsLabel.setFont(new Font("Consolas",Font.BOLD,16));
        navLabel.setHorizontalAlignment(JLabel.CENTER);
        actionsLabel.setHorizontalAlignment(JLabel.CENTER);
        section3.add(navLabel);
        navButtons.setBackground(new Color(0xF7E3CC));

        first.addActionListener(e -> navigateTeacher(i=0));
        previous.addActionListener(e -> {
            if (i > 0) {
                navigateTeacher(--i);
            }
        });
        next.addActionListener(e -> {
            if (i < teachers.size() - 1) {
                navigateTeacher(++i);
            }
        });
        last.addActionListener(e -> navigateTeacher(i = teachers.size() -1));

        navButtons.add(first);
        navButtons.add(previous);
        navButtons.add(next);
        navButtons.add(last);
        section3.add(navButtons);
        section3.add(actionsLabel);
        actionButtons.setBackground(new Color(0xF7E3CC));

        updateButton.addActionListener(e -> {
            updateTeacherData(firstNameField.getText().trim(),lastNameField.getText().trim(),
                    (availableField.getText().trim()),Integer.parseInt(idField.getText()));
        });
        deleteButton.addActionListener(e -> {
            deleteTeacher(Integer.parseInt(idField.getText()));
        });
        coursesButton.addActionListener(e -> {
            List<Course> courses = getCourses(Integer.parseInt(idField.getText()));
            this.setVisible(false);
            new CoursesWindow(main,connection,courses,teachers.get(i),this);
        });

        actionButtons.add(updateButton);
        actionButtons.add(deleteButton);
        actionButtons.add(coursesButton);
        section3.add(actionButtons);
        JPanel container2 = new JPanel(new FlowLayout((FlowLayout.CENTER)));
        container2.setBackground(new Color(0xF7E3CC));
        container.add(section3);

        contentPane.add(welcomeMessage,BorderLayout.NORTH);
        contentPane.add(container,BorderLayout.CENTER);
        contentPane.add(container2,BorderLayout.SOUTH);
        this.add(contentPane);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void updateTeacherData (String firstName,String lastName,String availability,int teacherId) {
        String updateTeacher = "UPDATE teachers SET firstname = ?, lastname = ?,available = ? WHERE teacher_id = ?";
        try (PreparedStatement psUpdate = connection.prepareStatement(updateTeacher)) {
            if(main.checkData(firstName,lastName,availability)) {
                psUpdate.setString(1,firstName);
                psUpdate.setString(2,lastName);
                int availabilityInt = Integer.parseInt(availability);
                psUpdate.setInt(3,availabilityInt);
                psUpdate.setInt(4,teacherId);
                if(psUpdate.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this,"Επιτυχής ενημέρωση δεδομένων",
                            "Ενημέρωση",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Ανεπιτυχής ενημέρωση δεδομένων",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void navigateTeacher (int i) {
        Teacher teacher = teachers.get(i);
        idField.setText(String.valueOf(teacher.getTeacherId()));
        firstNameField.setText(teacher.getFirstName());
        lastNameField.setText(teacher.getLastName());
        availableField.setText(String.valueOf(teacher.getHoursAvailablePerWeek()));
    }

    private void deleteTeacher (int teacherId) {
        String deleteTeacher = "DELETE FROM teachers WHERE teacher_id = ?";
        try (PreparedStatement psDelete = connection.prepareStatement(deleteTeacher)) {
            psDelete.setInt(1,teacherId);
            int choice = JOptionPane.showConfirmDialog(this,"Η διαγραφή είναι μη αναστρέψιμη, Είστε σίγουροι;",
                    "Επιβεβαίωση",JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                psDelete.executeUpdate();
                JOptionPane.showMessageDialog(this,"Επιτυχής διαγραφή καθηγητή",
                        "Ενημέρωση",JOptionPane.INFORMATION_MESSAGE);
                teachers.remove(i);
                if (teachers.isEmpty()) {
                    this.dispose();
                    return;
                }
                i = Math.max(0,i-1);
                navigateTeacher(i);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Ανεπιτυχής διαγραφή καθηγητή",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private List<Course> getCourses (int teacherId) {
        List<Course> courses = new ArrayList<>();
        String searchCourses = "SELECT * FROM school.courses WHERE teacher_id = ?";
        try (PreparedStatement psCourses = connection.prepareStatement(searchCourses)) {
            psCourses.setInt(1,teacherId);
            ResultSet rs = psCourses.executeQuery();
            while (rs.next()) {
                courses.add(new Course(rs.getInt(1),rs.getString(2),
                        rs.getInt(3),rs.getInt(4),rs.getInt(5)));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Ανεπιτυχής αναζήτηση μαθημάτων",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return courses;
    }
}
