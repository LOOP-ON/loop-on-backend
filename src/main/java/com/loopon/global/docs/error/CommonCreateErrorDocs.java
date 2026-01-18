package com.loopon.global.docs.error;

import com.loopon.global.docs.error.errors.CommonBadRequestResponseDocs;
import com.loopon.global.docs.error.errors.CommonForbiddenResponseDocs;
import com.loopon.global.docs.error.errors.CommonInternalServerErrorResponseDocs;
import com.loopon.global.docs.error.errors.CommonUnAuthorizedResponseDocs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@CommonUnAuthorizedResponseDocs
@CommonForbiddenResponseDocs
@CommonBadRequestResponseDocs
@CommonInternalServerErrorResponseDocs
public @interface CommonCreateErrorDocs {
}
