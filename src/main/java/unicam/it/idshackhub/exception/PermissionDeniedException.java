package unicam.it.idshackhub.exception;

// 403
public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException(String message) { super(message); }
}
