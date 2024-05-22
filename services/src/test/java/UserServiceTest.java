import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.services.MailingService;
import ar.edu.itba.paw.services.TokenService;
import ar.edu.itba.paw.services.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ar.edu.itba.paw.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {


    private static final String PASSWORD = "passwordpassword";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";

    @Mock
    private UserDao mockDao;

    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private TokenService mockTokenService;

    @Mock
    private MailingService mockMailingService;
    @InjectMocks
    private UserServiceImpl userService = new UserServiceImpl();


    @Test
    public void testCreate() throws UserNotFoundException {
        //	1.	Setup!
        Mockito.when(mockDao.create(Mockito.eq(USERNAME), Mockito.eq(EMAIL), Mockito.eq(PASSWORD))).thenReturn(new User( USERNAME, PASSWORD, EMAIL,false, Locale.getDefault().getLanguage()));
        Mockito.when(mockPasswordEncoder.encode(Mockito.anyString())).thenReturn(PASSWORD);
        Mockito.when(mockTokenService.generateValidationToken(Mockito.anyLong())).thenReturn("token");
//        Mockito.when(mockMailingService.sendValidationEmail(Mockito.anyString(), Mockito.anyString(),"token"))
        // 	2.	"ejercito"	la	class	under	test
        User maybeUser = userService.create(USERNAME, EMAIL, PASSWORD);
        // 	3.	Asserts!
        Assert.assertNotNull(maybeUser);
        Assert.assertEquals(USERNAME, maybeUser.getUsername());
        Assert.assertEquals(PASSWORD, maybeUser.getPassword());
        Assert.assertEquals(EMAIL, maybeUser.getEmail());
    }



}

