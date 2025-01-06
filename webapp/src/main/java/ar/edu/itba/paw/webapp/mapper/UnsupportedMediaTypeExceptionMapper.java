package ar.edu.itba.paw.webapp.mapper;

import javax.inject.Singleton;
import javax.ws.rs.NotSupportedException;
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
public class UnsupportedMediaTypeExceptionMapper implements ExceptionMapper<NotSupportedException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnsupportedMediaTypeExceptionMapper.class);

    @Override
    public Response toResponse(NotSupportedException exception) {
        LOGGER.info("{}: {}", exception.getClass().getName(), exception.getMessage());
        return Response
                .status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                .entity(ErrorDTO.fromErrorMsg(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();

    }

}
