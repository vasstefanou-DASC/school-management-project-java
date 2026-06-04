public class Teacher {

    private int teacherId;
    private String firstName;
    private String lastName;
    private int hoursAvailablePerWeek;

    public Teacher(int teacherId, String firstName, String lastName, int hoursAvailablePerWeek) {

        this.teacherId = teacherId;
        this.firstName = firstName.toUpperCase().charAt(0) + firstName.substring(1).toLowerCase();
        this.lastName = lastName.toUpperCase().charAt(0) + lastName.substring(1).toLowerCase();
        this.hoursAvailablePerWeek = hoursAvailablePerWeek;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public int getHoursAvailablePerWeek() {
        return hoursAvailablePerWeek;
    }

    public void setHoursAvailablePerWeek(int hoursAvailablePerWeek) {
        this.hoursAvailablePerWeek = hoursAvailablePerWeek;
    }
}
