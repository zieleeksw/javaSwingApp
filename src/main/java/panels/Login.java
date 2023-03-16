package panels;

import dbconnection.SingletonConnection;
import model.User;
import util.JTextFieldLimit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Login {
    private JPanel loginPanel;
    private JTextField tfLoginField;
    private JPasswordField pfPasswordField;
    private JButton createYourAccountButton;
    private JButton logInButton;
    private JButton cancelButton;
    private final JFrame frame;
    private String login;
    private String password;
    private User user;

    public Login() {
        /* init components */
        frame = new JFrame("Login Form");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(450, 457));
        frame.setResizable(false);
        frame.add(loginPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        setLimitsToFieldTexts();
        cancelButton.addActionListener(e -> frame.dispose());
        frame.setVisible(true);
        // ## listeners of frame ##  //
        createYourAccountButton.addActionListener(e -> {
            frame.dispose();
            new SignUp();
        });
        logInButton.addActionListener(e -> {
            getValuesFromLabels();
            user = getAuthenticatedUser(login, password);
            if (user != null) {
                if (isReconnaissanceFormNeeded()) {
                    new ReconnaissanceForm(user);
                    frame.dispose();
                } else if (!isReconnaissanceFormNeeded()) {
                    System.out.println("DashBoard opened");
                         new Dashboard(user);
                    frame.dispose();
                }

            } else {
                System.out.println("Invalid login or password");
                JOptionPane.showMessageDialog(this.frame, "Invalid login or password", "Try Again", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /*
    function returning user object checking whether
    user with login = ? is in the database
     */
    private User getAuthenticatedUser(String login, String password) {
        user = null;
        try {
            Connection connection = SingletonConnection.getInstance().getConnection();

            String sql = "SELECT * FROM users WHERE login=?"; //
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(resultSet.getString("login"),
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("role"));
            }
            if (user.getPassword().equals(password)) {
                return user;
            }
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getValuesFromLabels() {
        login = tfLoginField.getText().toLowerCase();
        password = String.valueOf(pfPasswordField.getPassword());
    }

    /*
    private function to SignUp class that limits number of chars in
    text fields
    */
    private void setLimitsToFieldTexts() {
        tfLoginField.setDocument(new JTextFieldLimit(16));
        pfPasswordField.setDocument(new JTextFieldLimit(16));
    }

    /*
    A function that checks if 'ReconnaissanceForm' is
    needed by checking all the needed values in the database
     */
    private boolean isReconnaissanceFormNeeded() {
        try {
            Connection connection = SingletonConnection.getInstance().getConnection();
            String sql = "SELECT * FROM users WHERE login=?"; //
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(login, resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("role"),
                        resultSet.getString("Gender"),
                        resultSet.getString("dateOfBirth"),
                        resultSet.getInt("Height"),
                        resultSet.getDouble("currentWeight"),
                        resultSet.getDouble("targetWeight"),
                        resultSet.getDouble("weightPerWeek"),
                        resultSet.getInt("age"),
                        resultSet.getInt("caloricNeeds"),
                        resultSet.getString("activityLevel"),
                        resultSet.getString("dietStatus"));
            }
            preparedStatement.close();
            if (user.getGender() == null || user.getDateOfBirth() == null || user.getRole() == null ||
                    user.getHeight() == 0 || user.getCurrentWeight() == 0.0d || user.getTargetWeight() == 0.0d ||
                    user.getAge() == 0 || user.getCaloricNeeds() == 0 || user.getActivityLevel() == null ||
                    user.getDietStatus() == null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

