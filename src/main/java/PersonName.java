public class PersonName {
    final String fname;
    final String lname;

    public PersonName(String fname, String lname) {
        this.fname = fname;
        this.lname = lname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonName name = (PersonName) o;

        if (!fname.equals(name.fname)) return false;
        return lname.equals(name.lname);
    }

    @Override
    public int hashCode() {
        int result = fname.hashCode();
        result = 31 * result + lname.hashCode();
        return result;
    }
}
