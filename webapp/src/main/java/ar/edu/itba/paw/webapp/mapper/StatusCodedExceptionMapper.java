package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.exceptions.StatusCodedException;
import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

@Singleton
@Component
@Provider
public class StatusCodedExceptionMapper implements ExceptionMapper<StatusCodedException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(StatusCodedException exception) {
        LOGGER.error("{}: {}", exception.getClass().getName(), exception.getMessageCode());
        return Response
                .status(Response.Status.fromStatusCode(exception.getStatusCode()))
                .entity(ErrorDTO.fromErrorMsg(
                        messageSource.getMessage(exception.getMessageCode(), null, LocaleContextHolder.getLocale())))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
