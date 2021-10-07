import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Contact {

    public final PersonName name;
    public final String email;
    public final LocalDate birthday;

    public Contact(PersonName name, LocalDate birthday, String email) {
        this.name = name;
        this.email = email;
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (!name.equals(contact.name)) return false;
        if (!email.equals(contact.email)) return false;
        return birthday.equals(contact.birthday);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + email.hashCode();
        result = 37 * result + birthday.hashCode();
        return result;
    }

    public PersonName getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name=" + name +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                '}';
    }

    public LocalDate getNextBirthday(LocalDate today) {
        return Utils.getNextBirthday(today, birthday);
    }
}
