import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

/**
 * StudentManager is a class that manages the Students table in PostgresSQL and implements
 * simple SQL functions. This class has a GUI, which contains buttons for calling such methods
 * and displaying the current state of the table.
 */
public class StudentManager implements ActionListener {
    //Buttons for SQL methods
    JButton getStudentsButton;
    JButton addStudentButton;
    JButton updateEmailButton;
    JButton deleteStudentButton;

    //Text area (for displaying table)
    JTextArea textArea;

    //Instance of postgresSQL connection (used by methods that implement SQL)
    public Connection connection;

    /**
     * Constructor for StudentManager.
     * Initializes the GUI frame and the action listeners for buttons.
     */
    public StudentManager()
    {
        //Create the GUI
        JFrame frame = new JFrame("Simple GUI Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        //Create a button panel for the user actions
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,4));
        getStudentsButton = new JButton("Get all Students");
        addStudentButton = new JButton("Add Student");
        updateEmailButton = new JButton("Update Student Email");
        deleteStudentButton = new JButton("Delete Student");
        buttonPanel.add(getStudentsButton);
        buttonPanel.add(addStudentButton);
        buttonPanel.add(updateEmailButton);
        buttonPanel.add(deleteStudentButton);

        //Create a text area to display the table
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        Font font = new Font("Arial", Font.PLAIN, 16);
        textArea.setFont(font);

        //Add components to the frame
        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        //Create action listeners for the buttons
        getStudentsButton.addActionListener(this);
        addStudentButton.addActionListener(this);
        updateEmailButton.addActionListener(this);
        deleteStudentButton.addActionListener(this);

        //Finish frame setup
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900,300);
        frame.setResizable(true);
        frame.setVisible(true);

        //Close the postgresSQL connection on close
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Handles action event for buttons by prompting user for inputs (where applicable)
     * and calls corresponding methods.
     * @param e The event to be processed
     */
    public void actionPerformed(ActionEvent e)
    {
        Object obj = e.getSource();
        if(obj instanceof JButton)
        {
            JButton pressedButton = (JButton) obj;
            if(pressedButton == getStudentsButton)
            {
                getAllStudents();
            }
            else if(pressedButton == addStudentButton)
            {
                //Create an input dialog to collect student information for new row
                JTextField firstNameField = new JTextField(20);
                JTextField lastNameField = new JTextField(20);
                JTextField emailField = new JTextField(60);
                JTextField enrollmentDateField = new JTextField(10);

                JPanel panel = new JPanel(new GridLayout(8, 1));
                panel.add(new JLabel("First name:"));
                panel.add(firstNameField);
                panel.add(new JLabel("Last Name:"));
                panel.add(lastNameField);
                panel.add(new JLabel("Email:"));
                panel.add(emailField);
                panel.add(new JLabel("Enrollment Date:"));
                panel.add(enrollmentDateField);

                int selectedButton = JOptionPane.showConfirmDialog(null, panel, "Enter Student's Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                //Check that the user pressed ok (stops crash when cancel button is pressed)
                if(selectedButton == JOptionPane.OK_OPTION)
                {
                    //Add the student to the table with the user entered credentials
                    addStudent(firstNameField.getText(), lastNameField.getText(), emailField.getText(), enrollmentDateField.getText());
                }
            }
            else if(pressedButton == deleteStudentButton)
            {
                //Create an input dialog to collect student information for new row
                JTextField idField = new JTextField(20);
                JPanel panel = new JPanel(new GridLayout(2, 1));
                panel.add(new JLabel("Student ID:"));
                panel.add(idField);

                int selectedButton = JOptionPane.showConfirmDialog(null, panel, "Enter Student's Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                //Check that the user pressed ok (stops crash when cancel button is pressed)
                if(selectedButton == JOptionPane.OK_OPTION)
                {
                    //Remove the student with the specified id
                    deleteStudent(idField.getText());
                }
            }
            else if(pressedButton == updateEmailButton)
            {
                //Create an input dialog to collect student information for new row
                JTextField idField = new JTextField(20);
                JTextField newEmailField = new JTextField(20);
                JPanel panel = new JPanel(new GridLayout(4, 1));
                panel.add(new JLabel("Student ID:"));
                panel.add(idField);
                panel.add(new JLabel(("New Email:")));
                panel.add(newEmailField);

                int selectedButton = JOptionPane.showConfirmDialog(null, panel, "Enter Student's Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                //Check that the user pressed ok (stops crash when cancel button is pressed)
                if(selectedButton == JOptionPane.OK_OPTION)
                {
                    //Remove the student with the specified id
                    updateStudentEmail(idField.getText(), newEmailField.getText());
                }
            }
        }
    }

    /**
     * Print the current state of the table.
     */
    public void getAllStudents()
    {
        try
        {
            //Clear the text area from the previous display
            textArea.setText("");

            //Get data from table
            String getTableSQL = "SELECT * FROM Students";
            ResultSet results = connection.prepareStatement(getTableSQL).executeQuery();

            //Print the result in the GUI's text area
            textArea.append("id\tfirst_name\tlast_name\temail\t\tenrollment_date\n");
            while(results.next())
            {
                int id = results.getInt("student_id");
                String fName = results.getString("first_name");
                String lName = results.getString("last_name");
                String email = results.getString("email");
                Date enrollmentDate = results.getDate("enrollment_date");
                textArea.append(Integer.toString(id) + "\t" + fName + "\t" + lName + "\t" + email + "\t" + enrollmentDate.toString() + "\n");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Add a new student entry to the table.
     *
     * @param firstName The first name of the student being added to the table
     * @param lastName The last name of the student being added to the table
     * @param email The email address of the student being added to the table
     * @param enrollmentDate The enrollment date of the student being added to the table
     */
    public void addStudent(String firstName, String lastName, String email, String enrollmentDate)
    {
        try {
            //Create SQL statement to add new entry
            String addRowSQL = "INSERT INTO Students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(addRowSQL);

            //Set variable student credentials
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setDate(4, Date.valueOf(enrollmentDate)); //assumes date is properly formatted

            //Add the student
            pstmt.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Change the email address for a student currently in the table.
     *
     * @param studentId The student ID for the student whose email address is being changed
     * @param newEmail The student's new email address
     */
    public void updateStudentEmail(String studentId, String newEmail)
    {
        try
        {
            //Update the email for student with specified student id
            String updateSQL = "UPDATE Students SET email = ? WHERE student_id = ?;";
            PreparedStatement pstmt = connection.prepareStatement(updateSQL);
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, Integer.parseInt(studentId));
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Remove a student from the table.
     *
     * @param studentId The student ID for the student being removed
     */
    public void deleteStudent(String studentId)
    {
        try
        {
            //delete student with specified id
            String deleteSQL = "DELETE FROM Students WHERE student_id = " + studentId + ";";
            connection.createStatement().executeUpdate(deleteSQL);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Main method.
     * Creates an instance of the GUI and performs the connection to the
     * existing Student table in PostgresSQL (initialized with three students).
     *
     * @param args The arguments passed to the main method (not applicable)
     */
    public static void main(String[] args)
    {
        //Create the view
        StudentManager manager = new StudentManager();

        //Perform PostgresSQL database connection
        String url = "jdbc:postgresql://localhost:5432/Students";
        String user = "postgres";
        String password = "admin";

        try
        {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            if (conn != null)
            {
                //Save instance of statement (to be used by SQL methods)
                manager.connection = conn;
            }
            else
            {
                System.out.println("Failed to establish connection.");
            }
        }
        catch(ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }

    }
}
