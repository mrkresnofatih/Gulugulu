package com.mrkresnofatihdev.gulugulu.utilities;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class HashHelper {
    private String saltFormat;

    public HashHelper(String saltFormat) {
        this.saltFormat = saltFormat;
    }

    public String HashData(String plainData) {
        var salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(_SaltedPlainData(plainData), salt);
    }

    public boolean CheckHash(String plainData, String hashData) {
        return BCrypt.checkpw(_SaltedPlainData(plainData), hashData);
    }

    private String _SaltedPlainData(String plainData) {
        return String.format(saltFormat, plainData);
    }
}
