package ar.edu.itba.paw.webapp.excpetion;

public class PostNotFoundException extends RuntimeException{

        public PostNotFoundException() {
            super();
        }

        public PostNotFoundException(String message) {
            super(message);
        }
}
