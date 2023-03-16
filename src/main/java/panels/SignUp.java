package panels;

import dbconnection.SingletonConnection;
import model.User;
import util.JTextFieldLimit;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignUp extends JFrame {
    //FRAME
    private JTextField tfName;
    private JTextField tfSurname;
    private JTextField tfLogin;
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton registerButton;
    private JButton cancelButton;
    private JPanel registerPanel;
    private JButton backToLoginButton;
    private final JFrame frame;
    private String login;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String confirmPassword;
    private final String role;

    public SignUp() {
        role = "User";
        /* init components */
        frame = new JFrame("Create your account");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(450, 457));
        frame.setResizable(false);
        frame.add(registerPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        setLimitsToFieldTexts();
        // ## listeners of frame ##  //
        cancelButton.addActionListener(e -> frame.dispose());
        backToLoginButton.addActionListener(e -> {
            frame.dispose();
            new Login();
        });
        registerButton.addActionListener(e -> registerUser());
        frame.setVisible(true);
    }


    /*
    function that brings together all other functions responsible for registration
     */

    private void registerUser() {
        getValuesFromLabels();
        if (isFieldExistInDB(login, "login")) {
            JOptionPane.showMessageDialog(this,
                    "User with that login exists", "Login error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isFieldExistInDB(email, "email")) {
            JOptionPane.showMessageDialog(this,
                    "User with that email exists", "Login error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (checkLogin(login)) {
            JOptionPane.showMessageDialog(this,
                    "Enter correct syntax for login. You cannot use special sings. Login should have min 5 digits and max 16."
                    , "DigitError", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (checkName(name)) {
            JOptionPane.showMessageDialog(this,
                    "Enter correct syntax for name. You cannot use special sings. Login should have min 3 digits and max 16, first letter must be capital."
                    , "DigitError", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (checkSurname(surname)) {
            JOptionPane.showMessageDialog(this,
                    "Enter correct syntax for surname. You cannot use special sings. Surname should have min 3 digits and max 16, first letter must be capital."
                    , "DigitError", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (checkMail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Enter correct syntax for your email. Mail should have max 32 digits."
                    , "DigitError", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (checkPassword(password)) {
            JOptionPane.showMessageDialog(this,
                    "Enter correct syntax for password. Password should have min 8 digits and max 32, min 1 number and 1 special digit."
                    , "DigitError", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isPasswordsTheSame(password, confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Confirm password does not match",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = addUserToDataBase(login, name, surname, email, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome: " + name, "Login successfully", JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            new Login();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register new user", "Try again", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void getValuesFromLabels() {
        login = tfLogin.getText().toLowerCase();
        name = tfName.getText();
        surname = tfSurname.getText();
        email = tfEmail.getText().toLowerCase();
        password = String.valueOf(pfPassword.getPassword());
        confirmPassword = String.valueOf(pfConfirmPassword.getPassword());
    }

    /* private function to SignUp class that limits number of chars in
       text fields
     */
    private void setLimitsToFieldTexts() {
        tfLogin.setDocument(new JTextFieldLimit(16));
        tfName.setDocument(new JTextFieldLimit(16));
        tfSurname.setDocument(new JTextFieldLimit(16));
        tfEmail.setDocument(new JTextFieldLimit(24));
        pfPassword.setDocument(new JTextFieldLimit(16));
        pfConfirmPassword.setDocument(new JTextFieldLimit(16));
    }

    /*
        function that checks if any of user fields is already in db.
        it's necessary with username and email.
    */
    private boolean isFieldExistInDB(String duplicatedField, String columnInDB) {
        boolean isDuplicated = false;
        Connection connection = SingletonConnection.getInstance().getConnection();
        try {
            String sql = "SELECT * FROM users WHERE " + columnInDB + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, duplicatedField);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isDuplicated = true;
            }
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDuplicated;
    }

    /*
    A lot of boolean functions that is checking
    if fields in db are correctly with syntax
     */
    private boolean checkLogin(String login) {
        return (((login.length() < 5 || login.length() > 16) ||
                (!login.matches("^[A-Za-z0-9]+([A-Za-z0-9]*|[._-]?[A-Za-z0-9]+)*$"))));
    }

    private boolean checkName(String name) {
        return ((name.length() < 3 || name.length() > 16) || (!name.matches("[A-Z][a-z]+(-[a-zA-Z]+)*")));
    }

    private boolean checkSurname(String surname) {
        return ((surname.length() < 3 || surname.length() > 16) || (!surname.matches("^[A-Z][-a-zA-Z]+$")));
    }

    private boolean checkMail(String email) {
        return (email.length() < 24 && (!email.matches("^([\\w-]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$")));
    }

    private boolean checkPassword(String password) {
        return ((password.length() < 6 || password.length() > 16) || (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,32}$")));
    }

    private boolean isPasswordsTheSame(String password, String confirmPassword) {
        return !password.equals(confirmPassword);
    }

    /*
    private function that add row with user params to db and returning
    user object
     */
    private User addUserToDataBase(String login, String name, String surname, String email, String password) {
        User user = null;
        try {
            Connection connection = SingletonConnection.getInstance().getConnection();
            String sql = "INSERT INTO users (login, name, surname, email, password, role) " +
                    "VALUES (?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, surname);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, password);
            preparedStatement.setString(6, role);
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User(login, name, surname, email, password, role);
            }
            connection.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}

