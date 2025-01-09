package ar.edu.itba.paw.webapp.mapper;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.webapp.dto.ErrorDTO;

@Singleton
@Component
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException exception) {
        LOGGER.error("{}: {}", exception.getClass().getName(), exception.getMessage());

        return Response
                .status(exception.getResponse().getStatus())
                .entity(ErrorDTO.fromErrorMsg(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
