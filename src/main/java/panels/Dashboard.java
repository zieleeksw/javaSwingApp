package panels;

import dbconnection.SingletonConnection;
import model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import util.Calculator;
import util.JTextFieldLimit;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Dashboard extends JFrame {
    private JButton ForumButton;
    private JButton accountButton;
    private JButton adminButton;
    private JPanel JPanel;
    private JPanel menuPanel;
    private JPanel adminPanel;
    private JPanel TODOPanel;
    private JLabel tfBMI;
    private JLabel tfGender;
    private JTextField tfCurrentWeight;
    private JTextField tfTargetWeight;
    private JLabel tfWelcome;
    private JTextField tfWeightPerWeek;
    private JLabel tfFinish;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton logOutButton;
    private JLabel tfHeight;
    private JLabel tfBirthDate;
    private JLabel tfYourWeightPerWeek;
    private JLabel tfQuote;
    private JLabel tfDietStatus;
    private JLabel tfCaloricNeeds;
    private final JFrame frame;
    private final User user;
    private double currentWeight;
    private double targetWeight;
    private double weightPerWeek;
    private int caloricNeeds;

    public Dashboard(User user) {
        /* init components */
        this.user = user;
        currentWeight = user.getCurrentWeight();
        targetWeight = user.getTargetWeight();
        weightPerWeek = user.getWeightPerWeek();
        frame = new JFrame("MyFitApp");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 800));
        frame.setResizable(false);
        frame.setContentPane(JPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        menuPanel.setVisible(true);
        setTextLimitations();
        setStuffIntoLabels();
        if (user.getRole().equals("Admin")) {
            adminButton.setVisible(true);
        } else adminButton.setVisible(false);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);

        // ## listeners of frame ##  //
        adminButton.addActionListener(e -> {
            TODOPanel.setVisible(true);
            adminPanel.setVisible(true);
            menuPanel.setVisible(false);
        });
        accountButton.addActionListener(e -> {
            TODOPanel.setVisible(true);
            adminPanel.setVisible(true);
            menuPanel.setVisible(true);
        });
        logOutButton.addActionListener(e -> {
            frame.dispose();
            new Login();
        });
        saveButton.addActionListener(e -> {
            if (!(fieldNotEmpty(tfCurrentWeight, tfTargetWeight, tfWeightPerWeek))) {
                JOptionPane.showMessageDialog(frame, "Field cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentWeight = Double.parseDouble(tfCurrentWeight.getText());
            targetWeight = Double.parseDouble(tfTargetWeight.getText());
            if (weightPerWeekCheck(tfCurrentWeight, tfTargetWeight, tfWeightPerWeek)) {
                JOptionPane.showMessageDialog(frame, "You cannot set WPW 0.0 while target and current  weight" +
                        "are different", "Error", JOptionPane.ERROR_MESSAGE);
                tfWeightPerWeek.setText(Double.toString(weightPerWeek));
                return;
            }
            if (!(weightPerWeekCheck(tfCurrentWeight, tfTargetWeight))) {
                JOptionPane.showMessageDialog(frame, "WPW has been set to 0.0 ", "Error", JOptionPane.INFORMATION_MESSAGE);
                weightPerWeek = Double.parseDouble(tfWeightPerWeek.getText());
                user.setWeightPerWeek(0.0d);
                tfWeightPerWeek.setText(Double.toString(user.getWeightPerWeek()));
            } else {
                weightPerWeek = Double.parseDouble(tfWeightPerWeek.getText());
                user.setWeightPerWeek(weightPerWeek);
            }
            user.setCurrentWeight(currentWeight);
            user.setTargetWeight(targetWeight);
            tfBMI.setText((Double.toString(Calculator.calculateBMI(user.getHeight(), user.getCurrentWeight()))));
            if (user.getWeightPerWeek() > 0)
                tfFinish.setText(dietFinish(user.getCurrentWeight(), user.getTargetWeight(), user.getWeightPerWeek()));
            updateWeightInDB(user.getCurrentWeight(), user.getTargetWeight(), user.getWeightPerWeek());
            saveButton.setVisible(false);
            cancelButton.setVisible(false);
        });
        // listeners that calculate BMI and finish time in present time
        // weightListeners(tfCurrentWeight, user.getCurrentWeight());
        weightListeners(tfTargetWeight, user.getTargetWeight());
        weightListeners(tfWeightPerWeek, user.getWeightPerWeek());
        tfCurrentWeight.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                weightListenerMethod(tfCurrentWeight, currentWeight);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                weightListenerMethod(tfCurrentWeight, currentWeight);

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                weightListenerMethod(tfCurrentWeight, currentWeight);
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
        tfTargetWeight.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.' || c == ',')) {
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
    }

    public void doINeedToShowButtons(JTextField jTF, double weight) {
        System.out.println(jTF.getText() + " " + weight);
        saveButton.setVisible(Double.parseDouble(jTF.getText()) != weight);
        cancelButton.setVisible(Double.parseDouble(jTF.getText()) != weight);
    }

    public void setTextLimitations() {
        tfWeightPerWeek.setDocument(new JTextFieldLimit(3));
        tfCurrentWeight.setDocument(new JTextFieldLimit(5));
        tfTargetWeight.setDocument(new JTextFieldLimit(5));
    }

    private void setStuffIntoLabels() {
        tfWelcome.setText("Welcome " + user.getName());
        tfTargetWeight.setText(Double.toString(user.getTargetWeight()));
        tfCurrentWeight.setText(Double.toString(user.getCurrentWeight()));
        tfWeightPerWeek.setText(Double.toString(user.getWeightPerWeek()));
        tfGender.setText(user.getGender());
        tfBirthDate.setText(user.getDateOfBirth());
        tfHeight.setText(user.getHeight() + "cm");
        tfBMI.setText((Double.toString(Calculator.calculateBMI(user.getHeight(), user.getCurrentWeight()))));
        tfQuote.setText(getQuote());
        if (user.getWeightPerWeek() > 0)
            tfFinish.setText(dietFinish(user.getCurrentWeight(), user.getTargetWeight(), user.getWeightPerWeek()));
        tfCaloricNeeds.setText(Integer.toString(user.getCaloricNeeds()));
        tfDietStatus.setText(user.getDietStatus());
    }

    private void updateWeightInDB(double currentWeight, double targetWeight, double weightPerWeek) {
        try {
            Connection connection = SingletonConnection.getInstance().getConnection();
            String sql = "UPDATE users set currentWeight = ?, targetWeight =? , weightPerWeek = ? WHERE login = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, currentWeight);
            preparedStatement.setDouble(2, targetWeight);
            preparedStatement.setDouble(3, weightPerWeek);
            preparedStatement.setString(4, user.getLogin());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean fieldNotEmpty(JTextField cw, JTextField tw, JTextField wpw) {
        return !cw.getText().isEmpty() && !tw.getText().isEmpty() && !wpw.getText().isEmpty();
    }

    private boolean weightPerWeekCheck(JTextField cw, JTextField tw) {
        return !(Double.parseDouble(cw.getText()) == Double.parseDouble(tw.getText()));
    }

    private boolean weightPerWeekCheck(JTextField cw, JTextField tw, JTextField wpw) {
        return (Double.parseDouble(cw.getText()) != Double.parseDouble(tw.getText())) && (Double.parseDouble(wpw.getText()) == 0.0d);
    }

    public static String getQuote() {
        String quote = "";
        int count = -1;
        try {
            URL url = new URL("https://type.fit/api/quotes");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode" + responseCode);
            } else {
                StringBuilder informationString = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
                scanner.close();
                JSONParser parse = new JSONParser();
                JSONArray dataObject = (JSONArray) parse.parse(String.valueOf(informationString));
                for (Object o : dataObject) {
                    count++;
                }
                JSONObject quoteText = (JSONObject) dataObject.get(Calculator.getRandomValue(count));
                quote = (String) quoteText.get("text");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quote;
    }

    private String dietFinish(double cw, double tw, double wpw) {
        if (wpw == 0 || cw == 0 || tw == 0) return "";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        double weightDiff = 0;
        double res2 = 0;
        int count = 0;
        if (cw > tw) weightDiff = cw - tw;
        else if (tw > cw) weightDiff = tw - cw;
        do {
            res2 += wpw;
            count++;
        } while (res2 < weightDiff);
        return dtf.format(now.plusWeeks(count));
    }

    private void updateBMI() {
        try {
            int height = user.getHeight();
            double weight = Double.parseDouble(tfCurrentWeight.getText());
            double BMI = Calculator.calculateBMI(height, weight);
            tfBMI.setText(String.valueOf(BMI));
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            tfBMI.setText("");
        }
    }

    private void weightListeners(JTextField tf, double weight) {
        tf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    if (tf.equals(tfCurrentWeight)) {
                        updateBMI();
                    }
                    doINeedToShowButtons(tf, weight);
                    tfFinish.setText(dietFinish(Double.parseDouble(tfCurrentWeight.getText()),
                            Double.parseDouble(tfTargetWeight.getText()),
                                    Double.parseDouble(tfWeightPerWeek.getText())));
                    caloricNeeds = (int) Calculator.caloricNeeds(user.getAge(), Double.parseDouble(tfCurrentWeight.getText()),
                            user.getHeight(), user.getGender(), user.getActivityLevel(),
                            Double.parseDouble(tfWeightPerWeek.getText()), tfDietStatus.getText());
                    tfCaloricNeeds.setText(Integer.toString(caloricNeeds));
                    tfDietStatus.setText(Calculator.getDietStatus(Double.parseDouble(tfCurrentWeight.getText()),
                            Double.parseDouble(tfTargetWeight.getText())));

                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    tf.setText("");
                    tfCaloricNeeds.setText("----");
                    tfFinish.setText("----");
                    tfDietStatus.setText("----");
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    if (tf.equals(tfCurrentWeight)) {
                        updateBMI();
                    }
                    doINeedToShowButtons(tf, weight);
                    tfFinish.setText(
                            dietFinish(Double.parseDouble(tfCurrentWeight.getText()), Double.parseDouble(tfTargetWeight.getText()),
                                    Double.parseDouble(tfWeightPerWeek.getText())));
                    caloricNeeds = (int) Calculator.caloricNeeds(user.getAge(), Double.parseDouble(tfCurrentWeight.getText()),
                            user.getHeight(), user.getGender(), user.getActivityLevel(),
                            Double.parseDouble(tfWeightPerWeek.getText()), tfDietStatus.getText());
                    tfCaloricNeeds.setText(Integer.toString(caloricNeeds));
                    tfDietStatus.setText(Calculator.getDietStatus(Double.parseDouble(tfCurrentWeight.getText()),
                            Double.parseDouble(tfTargetWeight.getText())));

                } catch (NumberFormatException ex) {
                    tf.setText("");
                    tfCaloricNeeds.setText("----");
                    tfFinish.setText("----");
                    tfDietStatus.setText("----");
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    if (tf.equals(tfCurrentWeight)) {
                        updateBMI();
                    }
                    doINeedToShowButtons(tf, weight);
                    tfFinish.setText(
                            dietFinish(Double.parseDouble(tfCurrentWeight.getText()), Double.parseDouble(tfTargetWeight.getText()),
                                    Double.parseDouble(tfWeightPerWeek.getText())));
                    caloricNeeds = (int) Calculator.caloricNeeds(user.getAge(), Double.parseDouble(tfCurrentWeight.getText()),
                            user.getHeight(), user.getGender(), user.getActivityLevel(),
                            Double.parseDouble(tfWeightPerWeek.getText()), tfDietStatus.getText());
                    tfCaloricNeeds.setText(Integer.toString(caloricNeeds));
                    tfDietStatus.setText(Calculator.getDietStatus(Double.parseDouble(tfCurrentWeight.getText()),
                            Double.parseDouble(tfTargetWeight.getText())));

                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    tf.setText("");
                    tfCaloricNeeds.setText("");
                    tfFinish.setText("");
                    tfDietStatus.setText("");
                }
            }
        });
    }

    private void weightListenerMethod(JTextField tf, double weight) {
        try {
            if (tf.equals(tfCurrentWeight)) {
                updateBMI();
            }
            doINeedToShowButtons(tf, weight);
            tfFinish.setText(
                    dietFinish(Double.parseDouble(tfCurrentWeight.getText()), Double.parseDouble(tfTargetWeight.getText()),
                            Double.parseDouble(tfWeightPerWeek.getText())));
            caloricNeeds = (int) Calculator.caloricNeeds(user.getAge(), Double.parseDouble(tfCurrentWeight.getText()),
                    user.getHeight(), user.getGender(), user.getActivityLevel(),
                    Double.parseDouble(tfWeightPerWeek.getText()), tfDietStatus.getText());
            tfCaloricNeeds.setText(Integer.toString(caloricNeeds));
            tfDietStatus.setText(Calculator.getDietStatus(Double.parseDouble(tfCurrentWeight.getText()),
                    Double.parseDouble(tfTargetWeight.getText())));

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            tf.setText("");
            tfCaloricNeeds.setText("----");
            tfFinish.setText("----");
            tfDietStatus.setText("----");
        }
    }
}
