package ar.edu.itba.paw.webapp.excpetion;

public class CommunityNotFoundException extends RuntimeException{

        public CommunityNotFoundException() {
            super();
        }

        public CommunityNotFoundException(String message) {
            super(message);
        }
}
