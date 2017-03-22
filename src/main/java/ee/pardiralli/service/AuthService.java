package ee.pardiralli.service;

import ee.pardiralli.wp.PrUsers;

public interface AuthService {

    boolean userIsWPAdmin(PrUsers wpUser);
}
