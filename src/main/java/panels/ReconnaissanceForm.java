package panels;

import dbconnection.SingletonConnection;
import model.User;
import util.Calculator;
import util.JTextFieldLimit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Period;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ReconnaissanceForm {
    private JFrame frame;
    private JPanel reconnaissanceFormPanel;
    private JLabel tfWelcomeUser;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JTextField tfDay;
    private JTextField tfMonth;
    private JTextField tfYear;
    private JTextField tfHeight;
    private JTextField tfCurrentWeight;
    private JTextField tfTargetWeight;
    private JTextField tfWeightPerWeek;
    private JButton nextButton;
    private JCheckBox acceptCheckBox;
    private JLabel tfw;
    private JRadioButton littleExcerciseButton;
    private JRadioButton lightExerciseButton;
    private JRadioButton moderateExerciseButton;
    private JRadioButton hardExerciseButton;
    private JRadioButton veryHardExerciseButton;
    private String gender;
    private int height;
    private double currentWeight;
    private double targetWeight;
    private double weightPerWeek;
    private String userDate;
    private String login;
    private String name;
    private String surname;
    private String email;
    private String password;
    private User user;
    private String activityLevel;
    private int age;
    private int caloricNeeds;
    private String dietStatus;
    private String role;

    public ReconnaissanceForm(User user) {
        /* init components */
        this.user = user;
        frame = new JFrame("MyFitApp");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setMinimumSize((new Dimension(600, 400)));
        frame.setResizable(false);
        frame.add(reconnaissanceFormPanel);
        frame.pack();
        genderButtonGroup();
        activityLevelButtonGroup();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        tfWelcomeUser.setText("Welcome " + user.getName());
        tfWelcomeUser.setVisible(true);
        setTextLimitations();
        // ## listeners of frame ##  //
        tfDay.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        tfMonth.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        tfYear.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        tfWeightPerWeek.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.' || c == ',')) {
                    e.consume();
                }
            }
        });
        tfTargetWeight.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.' || c == ',')) {
                    e.consume();
                }
            }
        });
        tfCurrentWeight.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.' || c == ',')) {
                    e.consume();
                }

            }
        });
        tfHeight.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        nextButton.addActionListener(e -> {
            if (!updateUserBMIStuff()) {
                frame.dispose();
                new Dashboard(user);
            }
        });
    }

    // #################################################### //
    private boolean updateUserBMIStuff() {
        getUserLoginParams();
        getCheckedValuesFromLabels();
        updateStuffInDB(gender, userDate, height, currentWeight, targetWeight, weightPerWeek, age, caloricNeeds,
                activityLevel, dietStatus, login);
        user = new User(login, name, surname, email, password, role, gender, userDate, height, targetWeight,
                currentWeight, weightPerWeek, age, caloricNeeds, activityLevel, dietStatus);
        if (user.getGender() != null || user.getDateOfBirth() != null || user.getHeight() == 0 ||
                user.getCurrentWeight() == 0.0d || user.getAge() == 0 || user.getCaloricNeeds() == 0 || user.getActivityLevel() != null || user.getDietStatus() != null) {
            return true;
        }
        updateStuffInDB(gender, userDate, height, currentWeight, targetWeight, weightPerWeek, age, caloricNeeds, activityLevel, dietStatus, login);
        return false;
    }

    private void updateStuffInDB(String gender,
                                 String dateOfBirth, int height, double currentWeight, double targetWeight,
                                 double weightPerWeek, int age, int caloricNeeds, String activityLevel, String dietStatus, String login) {
        try {
            Connection connection = SingletonConnection.getInstance().getConnection();
            String sql = "UPDATE users set gender = ?, dateOfBirth = ?, height = ?, currentWeight = ?, targetWeight =?," +
                    "weightPerWeek = ?, age=?, caloricNeeds=?, activityLevel=?, dietStatus=? WHERE login = ? ";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, gender);
            preparedStatement.setString(2, dateOfBirth);
            preparedStatement.setInt(3, height);
            preparedStatement.setDouble(4, currentWeight);
            preparedStatement.setDouble(5, targetWeight);
            preparedStatement.setDouble(6, weightPerWeek);
            preparedStatement.setInt(7, age);
            preparedStatement.setInt(8, caloricNeeds);
            preparedStatement.setString(9, activityLevel);
            preparedStatement.setString(10, dietStatus);
            preparedStatement.setString(11, login);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCheckedValuesFromLabels() {
        if (checkFieldsNotEmpty()) {
            JOptionPane.showMessageDialog(this.frame, "You have to fill all the fields", "Field alert", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isWeightCorrect(tfCurrentWeight.getText())) {
            JOptionPane.showMessageDialog(this.frame, "Enter correct currentWeight", "Current weight Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isWeightCorrect(tfTargetWeight.getText())) {
            JOptionPane.showMessageDialog(this.frame, "Enter correct targetWeight", " Target Weight alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isHeightCorrect(Integer.parseInt(tfHeight.getText()))) {
            JOptionPane.showMessageDialog(this.frame, "Enter correct height", " Height alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isWeighPerWeekCorrect(tfWeightPerWeek.getText())) {
            JOptionPane.showMessageDialog(this.frame, "Enter correct weight per week", " Weight per week alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!acceptCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this.frame, "You didn't accept license", "License error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (maleRadioButton.isSelected()) gender = "Male";
        else if (femaleRadioButton.isSelected()) gender = "Female";
        else if (!(femaleRadioButton.isSelected() && maleRadioButton.isSelected())) {
            JOptionPane.showMessageDialog(this.frame, "You have to choice your gender", "Gender alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (littleExcerciseButton.isSelected()) activityLevel = "Little active";
        else if (lightExerciseButton.isSelected()) activityLevel = "Light active";
        else if (moderateExerciseButton.isSelected()) activityLevel = "Moderate active";
        else if (hardExerciseButton.isSelected()) activityLevel = "Hard  active";
        else if (veryHardExerciseButton.isSelected()) activityLevel = "Very hard active";
        else {
            JOptionPane.showMessageDialog(this.frame, "You have to choice activity level", " Activity level alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            userDate = String.valueOf(
                    LocalDate.of(
                            Integer.parseInt(tfYear.getText()), Integer.parseInt(tfMonth.getText()), Integer.parseInt(tfDay.getText())));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.frame, "Enter correct date", " Date alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!isUser16yo(Integer.parseInt(tfYear.getText()), Integer.parseInt(tfMonth.getText()), Integer.parseInt(tfDay.getText()))) {
            JOptionPane.showMessageDialog(this.frame, "You don't have 16yo", "Age alert", JOptionPane.ERROR_MESSAGE);
            return;
        }

        height = Integer.parseInt(tfHeight.getText());
        currentWeight = Double.parseDouble(tfCurrentWeight.getText());
        targetWeight = Double.parseDouble(tfTargetWeight.getText());
        age = Calculator.calculateUserAge(Integer.parseInt(tfYear.getText()), Integer.parseInt(tfMonth.getText()), Integer.parseInt(tfDay.getText()));
        dietStatus = Calculator.getDietStatus(currentWeight, targetWeight);
        if (targetWeight == currentWeight) {
            weightPerWeek = 0.0d;
            JOptionPane.showMessageDialog(this.frame, "Your target and current weight are the same, we set your" +
                    "wpw to 0.0", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        caloricNeeds = (int) Calculator.caloricNeeds(age, currentWeight, height, gender, activityLevel, weightPerWeek, dietStatus);
    }


    /*
    Block of functions that checks all the params
     */
    public static boolean isWeightCorrect(String weight) {
        return !weight.matches("^(([1-3]\\d{2}|\\d{1,2})|(([1-3]\\d{2}|\\d{1,2})[\\.]\\d{1}))$");
    }

    public static boolean isWeighPerWeekCorrect(String weight) {
        return !(weight.matches("^0[.][0-9]") || weight.isEmpty());
    }

    public static boolean isHeightCorrect(int height) {
        return !(height <= 250);
    }

    private boolean checkFieldsNotEmpty() {
        return tfHeight.getText().isEmpty() || tfCurrentWeight.getText().isEmpty() || tfTargetWeight.getText().isEmpty()
                || tfWeightPerWeek.getText().isEmpty()
                || tfDay.getText().isEmpty() || tfMonth.getText().isEmpty() || tfYear.getText().isEmpty();
    }

    private static boolean isUser16yo(int year, int month, int day) {
        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate actualDate = LocalDate.now();
        return Period.between(birthDate, actualDate).getYears() >= 16;
    }

    /*functions that works on panel */
    private void getUserLoginParams() {
        login = user.getLogin();
        name = user.getName();
        surname = user.getSurname();
        email = user.getEmail();
        password = user.getPassword();
    }

    private void genderButtonGroup() {
        ButtonGroup group = new ButtonGroup();
        group.add(maleRadioButton);
        group.add(femaleRadioButton);
    }

    private void activityLevelButtonGroup() {
        ButtonGroup group1 = new ButtonGroup();
        group1.add(littleExcerciseButton);
        group1.add(lightExerciseButton);
        group1.add(moderateExerciseButton);
        group1.add(hardExerciseButton);
        group1.add(veryHardExerciseButton);
    }

    public void setTextLimitations() {
        tfDay.setDocument(new JTextFieldLimit(2));
        tfMonth.setDocument(new JTextFieldLimit(2));
        tfYear.setDocument(new JTextFieldLimit(4));
        tfHeight.setDocument(new JTextFieldLimit(3));
        tfWeightPerWeek.setDocument(new JTextFieldLimit(3));
        tfCurrentWeight.setDocument(new JTextFieldLimit(5));
        tfTargetWeight.setDocument(new JTextFieldLimit(5));
    }
}


