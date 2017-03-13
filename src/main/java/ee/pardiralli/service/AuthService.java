package ee.pardiralli.service;

import ee.pardiralli.wp.WpUsers;

public interface AuthService {

    boolean userIsWPAdmin(WpUsers wpUser);
}
