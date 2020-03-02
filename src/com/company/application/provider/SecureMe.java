package com.company.application.provider;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

@NameBinding
@Retention(value = RetentionPolicy.CLASS)
@Target({ TYPE, METHOD })
public @interface SecureMe {

}
