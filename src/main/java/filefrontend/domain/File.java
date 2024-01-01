package filefrontend.domain;

import java.util.Objects;

public class File {

    private final String name;
    private final String holder;
    private final Long size;
    private final String iv;

    /**
     * @param name the file's name
     * @param holder the storBackEnd name who hold the file
     */
    public File(final String name, final String holder, final Long size, final String iv) {
        this.name = name;
        this.holder = holder;
        this.size = size;
        this.iv = iv;
    }

    /**
     * @return the storBackEnd name who hold the file
     */
    public String getHolder() {
        return holder;
    }

    /**
     * @return the file's name
     */
    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public String getIv() {
        return iv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return name.equals(file.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
