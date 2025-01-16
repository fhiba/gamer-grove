import ar.edu.itba.paw.exceptions.NoSuchTokenException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.services.MailingService;
import ar.edu.itba.paw.services.TokenService;
import ar.edu.itba.paw.services.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ar.edu.itba.paw.models.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserDao mockUserDao;

    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private TokenService mockTokenService;
    @Mock
    private SecurityContextHolder mockSecurityContextHolder;

    @Mock
    private MailingService mockMailingService;
    @InjectMocks
    private UserServiceImpl userService = new UserServiceImpl();

    public static final Long USER_ID = 1L;
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String TOKEN = "token";

    @Test
    public void testCreateSuccess() throws UserNotFoundException {

        final User user = new User(USERNAME, ENCODED_PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockUserDao.create(eq(USERNAME), eq(EMAIL), anyString())).thenReturn(user);
        when(mockPasswordEncoder.encode(eq(PASSWORD))).thenReturn(ENCODED_PASSWORD);

        User result = userService.create(USERNAME, EMAIL, PASSWORD);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(EMAIL, result.getEmail());
    }

    @Test
    public void testResetPasswordSuccess() throws NoSuchTokenException, UserNotFoundException {
        final User user = new User(USERNAME, ENCODED_PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockTokenService.getUserIdFromToken(TOKEN, "ResetPass")).thenReturn(Optional.of(USER_ID));
        when(mockUserDao.findById(USER_ID)).thenReturn(Optional.of(user));
        when(mockPasswordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");

        userService.resetPassword(TOKEN, PASSWORD);

    }

    @Test(expected = NoSuchTokenException.class)
    public void testResetPasswordNoSuchToken() throws NoSuchTokenException, UserNotFoundException {
        String password = "testPassword";

        when(mockTokenService.getUserIdFromToken(TOKEN, "ResetPass")).thenReturn(Optional.empty());

        userService.resetPassword(TOKEN, password);
    }

    @Test
    public void testVerifyUserSuccess() throws NoSuchTokenException {
        final User user = new User(USERNAME, ENCODED_PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockTokenService.getUserFromToken(TOKEN, "Validation")).thenReturn(Optional.of(user));

        User result = userService.verifyUser(TOKEN);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
    }

    @Test(expected = NoSuchTokenException.class)
    public void testVerifyUserNoSuchToken() throws NoSuchTokenException {

        when(mockTokenService.getUserFromToken(TOKEN, "Validation")).thenReturn(Optional.empty());

        userService.verifyUser(TOKEN);
    }

    @Test
    public void testStartResetPasswordSuccess() throws UserNotFoundException {
        final User user = new User(USERNAME, ENCODED_PASSWORD, EMAIL, true, "es", true);
        user.setId(USER_ID);

        when(mockUserDao.findByEmail(eq(EMAIL))).thenReturn(Optional.of(user));
        when(mockTokenService.generateResetToken(anyLong())).thenReturn(TOKEN);

        boolean result = userService.startResetPassword(EMAIL);

        assertTrue(result);
    }

    @Test
    public void testStartResetPasswordUserNotFound() throws UserNotFoundException {

        when(mockUserDao.findByEmail(eq(EMAIL))).thenReturn(Optional.empty());

        boolean result = userService.startResetPassword(EMAIL);

        assertFalse(result);
    }

}
