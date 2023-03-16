package model;

public class User {
    private String login;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String role;
    // TARGET ON BMI
    private String gender;
    private String dateOfBirth;
    private int height;
    private double currentWeight;
    private  double targetWeight;
    private double weightPerWeek;
    private int age;
    private int caloricNeeds;
    private String activityLevel;
    private String dietStatus;

    public User(String login, String name, String surname, String email, String password, String role) {
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String login, String name, String surname, String email, String password) {
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    public User(String login, String name, String surname, String email, String password, String role, String gender,
                String dateOfBirth, int height, double currentWeight, double targetWeight, double weightPerWeek, int age,
                int caloricNeeds, String activityLevel, String dietStatus) {
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.currentWeight = currentWeight;
        this.targetWeight = targetWeight;
        this.weightPerWeek = weightPerWeek;
        this.age = age;
        this.caloricNeeds = caloricNeeds;
        this.activityLevel = activityLevel;
        this.dietStatus = dietStatus;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public double getWeightPerWeek() {
        return weightPerWeek;
    }

    public void setWeightPerWeek(double weightPerWeek) {
        this.weightPerWeek = weightPerWeek;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getCaloricNeeds() {
        return caloricNeeds;
    }

    public void setCaloricNeeds(int caloricNeeds) {
        this.caloricNeeds = caloricNeeds;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getDietStatus() {
        return dietStatus;
    }

    public void setDietStatus(String dietStatus) {
        this.dietStatus = dietStatus;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", height=" + height +
                ", currentWeight=" + currentWeight +
                ", targetWeight=" + targetWeight +
                ", weightPerWeek=" + weightPerWeek +
                ", age=" + age +
                ", caloricNeeds=" + caloricNeeds +
                ", activityLevel='" + activityLevel + '\'' +
                ", dietStatus='" + dietStatus + '\'' +
                '}';
    }
}
