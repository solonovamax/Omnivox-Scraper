package com.solostudios.omnivoxscraper.impl.utils.config;

import lombok.*;
import lombok.experimental.Accessors;


/**
 * A Class holding the config for the domain and the
 *
 * @author solonovamax
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Accessors(chain = true)
public class AuthConfig {
    /**
     * The username of the user to log in as.
     *
     * @param username The username you want to log in as.
     * @return The username to log in as.
     */
    private String username;
    /**
     * The password of the user to log in as.
     *
     * @param username The password you want to use to log in.
     * @return The password to use to log in.
     */
    private String password;
}
