package ru.frostman.scalable.reactor;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public class ReactorException extends RuntimeException{
    public ReactorException() {
        super();
    }

    public ReactorException(String message) {
        super(message);
    }

    public ReactorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReactorException(Throwable cause) {
        super(cause);
    }
}
