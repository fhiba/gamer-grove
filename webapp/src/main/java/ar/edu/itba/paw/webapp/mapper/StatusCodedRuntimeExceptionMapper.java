package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.StatusCodedRuntimeException;
import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Singleton
@Component
@Provider
public class StatusCodedRuntimeExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<StatusCodedRuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionMapper.class);

    @Override
    public Response toResponse(StatusCodedRuntimeException exception) {
        LOGGER.error("{}: {}", exception.getClass().getName(), exception.getMessage());

        return Response
                .status(Response.Status.fromStatusCode(exception.getStatusCode()))
                .entity(ErrorDTO.fromErrorMsg(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
