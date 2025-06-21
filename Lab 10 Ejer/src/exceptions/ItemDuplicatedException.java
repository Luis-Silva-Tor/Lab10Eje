package exceptions;

public class ItemDuplicatedException extends RuntimeException {
    public ItemDuplicatedException(String message) {
        super(message);
    }
}
