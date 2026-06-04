import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CoursesWindow extends JFrame {
    private Connection connection;
    private List<Course> courses;
    private Teacher teacher;
    private int i = 0;
    private JTextField idField;
    private JTextField courseNameField;
    private JTextField semesterField;
    private JTextField hoursField;
    private MainWindow main;
    private SearchResultsWindow results;
    public CoursesWindow(MainWindow main,Connection connection,List<Course> courses,Teacher teacher,SearchResultsWindow results) {
        this.main = main;
        this.connection = connection;
        this.courses = courses;
        this.teacher = teacher;
        this.results = results;
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                results.setVisible(true); // ενεργοποίησε ξανά όταν κλείσει
            }
        });

        this.setTitle("Φόρμα Μαθημάτων");
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        JPanel section2 = new JPanel(new GridLayout(4, 2, 20, 20));
        JLabel welcomeMessage = new JLabel("Φόρμα μαθημάτων " + teacher.getFirstName() + " "
            + teacher.getLastName());
        JLabel idLabel = new JLabel("ID: ");
        idField = new JTextField(15);
        JLabel courseNameLabel = new JLabel("Όνομα μαθήματος: ");
        courseNameField = new JTextField(15);
        JLabel semesterLabel = new JLabel("Εξάμηνο: ");
        semesterField = new JTextField(15);
        JLabel hoursLabel = new JLabel("Ώρες/Εβδομάδα: ");
        hoursField = new JTextField(15);
        JPanel section3 = new JPanel(new GridLayout(4,1,0,20));
        JLabel navLabel = new JLabel("Πλοήγηση Αποτελεσμάτων");
        JPanel navButtons = new JPanel(new GridLayout(1,4,10,0));
        JButton first = new JButton("| <");
        JButton previous = new JButton("<");
        JButton next = new JButton(">");
        JButton last = new JButton("> |");
        JLabel actionsLabel = new JLabel("Ενέργειες");
        JPanel actionButtons = new JPanel(new GridLayout(1,3,10,0));
        JButton addButton = new JButton("Προσθήκη");
        JButton removeButton = new JButton("Αφαίρεση");

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
        courseNameLabel.setFont(new Font("Consolas",Font.BOLD,16));
        courseNameField.setFont(new Font("Consolas",Font.PLAIN,16));
        courseNameField.setEditable(false);
        courseNameField.setFocusable(false);
        semesterLabel.setFont(new Font("Consolas",Font.BOLD,16));
        semesterField.setFont(new Font("Consolas",Font.PLAIN,16));
        semesterField.setEditable(false);
        semesterField.setFocusable(false);
        hoursLabel.setFont(new Font("Consolas",Font.BOLD,16));
        hoursField.setFont(new Font("Consolas",Font.PLAIN,16));
        hoursField.setEditable(false);
        hoursField.setFocusable(false);
        if (!courses.isEmpty()) {
            navigateCourses(i = 0);
        } else {
            JOptionPane.showMessageDialog(this, "Δεν υπάρχουν μαθήματα για τον καθηγητή",
                    "Ενημέρωση", JOptionPane.INFORMATION_MESSAGE);
        }

        section2.setBackground(new Color(0xF7E3CC));
        section2.add(idLabel);
        section2.add(idField);
        section2.add(courseNameLabel);
        section2.add(courseNameField);
        section2.add(semesterLabel);
        section2.add(semesterField);
        section2.add(hoursLabel);
        section2.add(hoursField);
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

        first.addActionListener(e -> navigateCourses(i=0));
        previous.addActionListener(e -> {
            if (i > 0) {
                navigateCourses(--i);
            }
        });
        next.addActionListener(e -> {
            if (i < courses.size() - 1) {
                navigateCourses(++i);
            }
        });
        last.addActionListener(e -> navigateCourses(i = courses.size() -1));

        navButtons.add(first);
        navButtons.add(previous);
        navButtons.add(next);
        navButtons.add(last);
        section3.add(navButtons);
        section3.add(actionsLabel);
        actionButtons.setBackground(new Color(0xF7E3CC));

        addButton.addActionListener(e -> {
            optionAddCourseWindow();
        });
        removeButton.addActionListener(e -> {
            if (courses.isEmpty()) {
                return;
            }
            removeCourseFromTeacher(teacher.getHoursAvailablePerWeek(),teacher.getTeacherId(),
                    courses.get(i).getHoursPerWeek(),courses.get(i).getCourseId());
        });

        actionButtons.add(addButton);
        actionButtons.add(removeButton);
        section3.add(actionButtons);
        JPanel container2 = new JPanel(new FlowLayout((FlowLayout.CENTER)));
        container2.setBackground(new Color(0xF7E3CC));
        container2.add(section3);

        contentPane.add(welcomeMessage,BorderLayout.NORTH);
        contentPane.add(container,BorderLayout.CENTER);
        contentPane.add(container2,BorderLayout.SOUTH);
        this.add(contentPane);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void optionAddCourseWindow() {
        JDialog dialog = new JDialog(this,"Προσθήκη Μαθήματος",true);
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 0));

        JLabel courseNameLabel = new JLabel("Όνομα μαθήματος: ");
        JTextField courseNameField = new JTextField(15);
        courseNameLabel.setFont(new Font("Consolas",Font.BOLD,16));
        courseNameField.setFont(new Font("Consolas",Font.PLAIN,16));
        dialogPanel.setPreferredSize(new Dimension(480,200));
        dialogPanel.setBackground(new Color(0xF7E3CC));
        dialog.setResizable(false);

        JLabel addMessage = new JLabel("Προσθήκη/Αφαίρεση Μαθήματος");
        addMessage.setPreferredSize(new Dimension(400, 80));
        addMessage.setHorizontalAlignment(JLabel.CENTER);
        addMessage.setVerticalAlignment(JLabel.CENTER);
        addMessage.setFont(new Font("Consolas", Font.BOLD, 20));

        JPanel center = new JPanel(new FlowLayout());
        center.setBackground(new Color(0xF7E3CC));
        center.add(courseNameLabel);
        center.add(courseNameField);

        JPanel south = new JPanel(new FlowLayout());
        south.setBackground(new Color(0xF7E3CC));
        JButton confirmButton = new JButton("Προσθήκη");
        confirmButton.addActionListener(e -> {
            Course course = findCourseByTitle(courseNameField.getText().trim());
            if (course != null) {
                if (course.getTeacherId() == teacher.getTeacherId()) {
                    JOptionPane.showMessageDialog(this, "Ο καθηγητής διδάσκει ήδη αυτό το μάθημα",
                            "Προειδοποίηση", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (course.getTeacherId() != 0) {
                    int choice = JOptionPane.showConfirmDialog(this,"Θέλετε σίγουρα να γίνει αλλαγή καθηγητή;",
                            "Επιβεβαίωση",JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                        return;
                    }
                    if (choice == JOptionPane.YES_OPTION) {
                        updateTeacherData(
                                getOldTeacherAvailability(course.getTeacherId()) + course.getHoursPerWeek(),
                                course.getTeacherId()
                        );
                    }
                }
                addCourseToTeacher(teacher.getHoursAvailablePerWeek(), teacher.getTeacherId(),
                        course);
            }
        });
        south.add(confirmButton);

        dialogPanel.add(addMessage,BorderLayout.NORTH);
        dialogPanel.add(center,BorderLayout.CENTER);
        dialogPanel.add(south,BorderLayout.SOUTH);

        dialog.add(dialogPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void navigateCourses (int i) {
        if (courses.isEmpty()) {
            return;
        }
        Course course = courses.get(i);
        idField.setText(String.valueOf(course.getCourseId()));
        courseNameField.setText(course.getCourseTitle());
        semesterField.setText(String.valueOf(course.getSemester()));
        hoursField.setText(String.valueOf(course.getHoursPerWeek()));
    }

    private void updateTeacherData (int availability,int teacherId) {
        String updateAvailability = "UPDATE teachers SET available = ? WHERE teacher_id = ?";
        try (PreparedStatement psUpdate = connection.prepareStatement(updateAvailability)) {
            psUpdate.setInt(1,availability);
            psUpdate.setInt(2,teacherId);
            psUpdate.executeUpdate();
            JOptionPane.showMessageDialog(this,"Επιτυχής ενημέρωση διαθεσιμότητας καθηγητή",
                    "Ενημέρωση",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Ανεπιτυχής ενημέρωση διαθεσιμότητας καθηγητή",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addCourseToTeacher (int availability,int teacherId,Course course) {
        if (availability-course.getHoursPerWeek() < 0){
            JOptionPane.showMessageDialog(this,"Ο καθηγητής δεν έχει διαθέσιμες ώρες",
                    "Προειδοποίηση",JOptionPane.WARNING_MESSAGE);
            return;
        }
        String addCourseToTeacher = "UPDATE courses SET teacher_id = ? WHERE course_id = ?";
        try (PreparedStatement psAdd = connection.prepareStatement(addCourseToTeacher)) {
            psAdd.setInt(1,teacherId);
            psAdd.setInt(2,course.getCourseId());
            if (psAdd.executeUpdate() > 0){
                updateTeacherData(availability-course.getHoursPerWeek(),teacherId);
                teacher.setHoursAvailablePerWeek(availability-course.getHoursPerWeek());
                courses.add(course);
                if (courses.size() == 1) {
                    navigateCourses(i=0);
                }
                JOptionPane.showMessageDialog(this,"Επιτυχής προσθήκη μαθήματος",
                        "Ενημέρωση",JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Ανεπιτυχής προσθήκη μαθήματος",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private int getOldTeacherAvailability(int teacherId) {
        String query = "SELECT available FROM teachers WHERE teacher_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Course findCourseByTitle(String courseTitle) {
        String findCourse = "SELECT * FROM courses WHERE LOWER(coursetitle) = ?";
        try (PreparedStatement psCourse = connection.prepareStatement(findCourse)) {
            psCourse.setString(1,courseTitle.toLowerCase());
            ResultSet rs = psCourse.executeQuery();
            if (rs.next()) {
                return (new Course(rs.getInt(1),rs.getString(2),
                        rs.getInt(3),rs.getInt(4),rs.getInt(5)));
            }
            JOptionPane.showMessageDialog(this,"Δεν υπάρχει μάθημα με αυτό τον τίτλο",
                    "Προειδοποίηση",JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Ανεπιτυχής αναζήτηση μαθήματος",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    public void removeCourseFromTeacher (int availability,int teacherId,int hoursPerWeek,int courseId) {
        String removeCourse = "UPDATE courses SET teacher_id = NULL WHERE course_id = ?";
        try (PreparedStatement psRemove = connection.prepareStatement(removeCourse)) {
            psRemove.setInt(1,courseId);
            if (psRemove.executeUpdate() > 0) {
                updateTeacherData(availability+hoursPerWeek,teacherId);
                teacher.setHoursAvailablePerWeek(availability+hoursPerWeek);
                courses.remove(i);
                if (courses.isEmpty()) {
                    idField.setText("");
                    courseNameField.setText("");
                    semesterField.setText("");
                    hoursField.setText("");
                } else {
                    i = Math.max(0, i - 1);
                    navigateCourses(i);
                }
                JOptionPane.showMessageDialog(this,"Επιτυχής αφαίρεση μαθήματος",
                        "Ενημέρωση",JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Ανεπιτυχής αφαίρεση μαθήματος",
                    "Σφάλμα",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
