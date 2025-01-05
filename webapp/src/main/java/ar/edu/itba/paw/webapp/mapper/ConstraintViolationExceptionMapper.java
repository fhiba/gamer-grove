package ar.edu.itba.paw.webapp.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Singleton
@Component
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(prepareMessage(exception))
                .type("text/plain")
                .build();
    }

    private String prepareMessage(ConstraintViolationException exception) {

        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
            message.append(cv.getPropertyPath() + " " + cv.getMessage() + "\n");
        }
        return message.toString();
    }
}
