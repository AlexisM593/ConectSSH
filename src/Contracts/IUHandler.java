package Contracts;

public interface IUHandler {
    void setText(String id, String text);
    void setSrc(String id, String src);
    boolean isEnabled(String id);
}
