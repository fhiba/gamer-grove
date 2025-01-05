package ar.edu.itba.paw.webapp.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import ar.edu.itba.paw.webapp.dto.ErrorValidationDTO;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<ErrorValidationDTO> errors = new ArrayList<>();

        e.getConstraintViolations().forEach(violation -> errors.add(
                ErrorValidationDTO.fromValidationError(getViolationPropertyName(violation), violation.getMessage())));

        LOGGER.error("{}: {}",
                e.getClass().getName(),
                e.getConstraintViolations());

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<Collection<ErrorValidationDTO>>(errors) {
                })
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String getViolationPropertyName(ConstraintViolation<?> violation) {
        final String propertyPath = violation.getPropertyPath().toString();
        return propertyPath.substring(propertyPath.lastIndexOf(".") + 1);
    }
}
