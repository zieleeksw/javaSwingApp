package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Random;

public class Calculator {
    public static double calculateBMI(int height, double weight) {
        double bmi;
        double h = height;
        bmi = weight / Math.pow(h / 100, 2);
        return round(bmi);
    }

    public static double round(double value) {
        int precision = 2;
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(precision, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public static int getRandomValue(int max) {
        Random random = new Random();
        return random.nextInt(max + 1);
    }

    public static int calculateUserAge(int year, int month, int day) {
        LocalDate birthDate = LocalDate.of(year, month, day);
        LocalDate actualDate = LocalDate.now();
        return Period.between(birthDate, actualDate).getYears();
    }

    public static double caloricNeeds(int age, double weight, int height, String gender, String activityLevel,
                                      double weightPerWeek, String dietStatus) {
        // BMR formula for men: 66 + (6.2 x weight in pounds) + (12.7 x height in inches) - (6.76 x age in years)
        // BMR formula for women: 655.1 + (4.35 x weight in pounds) + (4.7 x height in inches) - (4.7 x age in years)

        double bmr = 0;
        double caloricNeeds = 0;
        if (gender.equals("Male")) {
            bmr = 66.5 + (13.7 * weight) + (5 * height) - (6.8 * age);
            System.out.println("Male");
        } else if (gender.equals("Female")) {
            bmr = 655 + (9.6 * weight) + (1.85 * height) - (4.7 * age);
            System.out.println("Female");
        }
        caloricNeeds = switch (activityLevel) {
            case "Little active" -> bmr * 1.2;
            case "Light active" -> bmr * 1.375;
            case "Moderate active" -> bmr * 1.55;
            case "Hard  active" -> bmr * 1.725;
            case "Very hard active" -> bmr * 1.9;
            default -> 0.0d;
        };
        if (dietStatus.equals("Bulking")) {
            caloricNeeds = caloricNeeds + (3500 * weightPerWeek) / 7;
        } else if (dietStatus.equals("Reduction")) {
            caloricNeeds = caloricNeeds - (3500 * weightPerWeek) / 7;
        } else return caloricNeeds;
        // The Harris-Benedict equation is used to calculate the total daily caloric needs
        // It takes into account the person's BMR and their activity level
        // 1.2 is the factor for sedentary individuals (little or no exercise)
        // 1.375 is the factor for lightly active individuals (light exercise/sports 1-3 days/week)
        // 1.55 is the factor for moderately active individuals (moderate exercise/sports 3-5 days/week)
        // 1.725 is the factor for very active individuals (hard exercise/sports 6-7 days a week)
        // 1.9 is the factor for extra active individuals (very hard exercise/sports & physical job or 2x training)

        return caloricNeeds;
    }

    public static String getDietStatus(double cw, double tw) {
        String status = "";
        if (cw == tw) status = "Weight maintenance";
        else if (cw > tw) status = "Reduction";
        else status = "Bulking";

        return status;
    }
}
