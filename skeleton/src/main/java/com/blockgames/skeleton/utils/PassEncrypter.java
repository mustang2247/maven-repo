package com.blockgames.skeleton.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class PassEncrypter {

    //private static final Logger log = LoggerFactory.getLogger( PassEncrypter.class );
    private static String prefixDefault = "";
    private static String suffixDefault = "";

    public static void init( String prefix, String suffix ) {
        prefixDefault = prefix;
        suffixDefault = suffix;
    }

    public static String encrypt( String prefix, String val, String suffix ) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance( "MD5" );
            md5.update( ( prefix + val + suffix ).getBytes() );
            byte[] buf = md5.digest();
            char[] digit = "0123456789ABCDEF".toCharArray();
            StringBuilder sb = new StringBuilder();
            for( byte b : buf ) {
                sb.append( digit[( b & 0xF0 ) >> 4] ).append( digit[b & 0xF] );
            }
            return sb.toString();
        }
        catch( NoSuchAlgorithmException e ) {
            log.error( e.getMessage(), e );
        }
        return "";
    }

    public static String encrypt( String val ) {
        return encrypt( prefixDefault, val, suffixDefault );
    }

}
