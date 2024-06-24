import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.TokenDao;
import ar.edu.itba.paw.services.TokenService;
import ar.edu.itba.paw.services.TokenServiceImpl;
import ar.edu.itba.paw.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceTest {
    @Mock
    private TokenDao mockTokenDao;

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private TokenServiceImpl tokenService;

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final Long USER_ID = 1L;
    public static final UUID uuid = UUID.randomUUID();

    @Test
    public void testGenerateValidationTokenSuccess() throws UserNotFoundException {
        final User user = new User(USERNAME, PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockUserService.findById(USER_ID)).thenReturn(Optional.of(user));

        String result = tokenService.generateValidationToken(USER_ID);

        assertNotNull(result);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGenerateValidationToken_UserNotFound() throws UserNotFoundException {

        when(mockUserService.findById(USER_ID)).thenReturn(Optional.empty());

        tokenService.generateValidationToken(USER_ID);
    }
}
