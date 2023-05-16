package com.xiaofu.aspect;

import com.xiaofu.annotation.EncryptField;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static com.xiaofu.enums.EncryptConstant.DECRYPT;
import static com.xiaofu.enums.EncryptConstant.ENCRYPT;

@Slf4j
@Aspect
@Component
public class EncryptHandler {

    @Autowired
    private StringEncryptor stringEncryptor;

    @Pointcut("@annotation(com.xiaofu.annotation.EncryptMethod)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        /**
         * 加密
         */
        encrypt(joinPoint);
        /**
         * 解密
         */
        Object decrypt = decrypt(joinPoint);
        return decrypt;
    }

    public void encrypt(ProceedingJoinPoint joinPoint) {

        try {
            Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
            Parameter[] parameters = method.getParameters();
            Object[] objects = joinPoint.getArgs();
            for (int i = 0; i < parameters.length; i++) {
                objects[i] = handler(objects[i],parameters[i],ENCRYPT);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Object decrypt(ProceedingJoinPoint joinPoint) {
        Object result = null;
        try {
            Object obj = joinPoint.proceed();
            if (obj != null) {
                    result = handler(obj,null, DECRYPT);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    private Object handler(Object obj,Parameter parameter, String type) throws IllegalAccessException {

        if (Objects.isNull(obj)) {
            return null;
        }
        if (obj instanceof String) {
            if(DECRYPT.equals(type)){
                return decryptValue(obj);
            }else {
                if(parameter!=null){
                    boolean hasSecureField = parameter.isAnnotationPresent(EncryptField.class);
                    if(hasSecureField){
                        obj = encryptValue(obj);
                    }
                }else {obj = encryptValue(obj);}
            }
        } else {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object realValue = field.get(obj);
                boolean hasSecureField = field.isAnnotationPresent(EncryptField.class);
                if (hasSecureField) {
                    if (realValue instanceof String) {
                        field.setAccessible(true);
                        String value;
                        if (DECRYPT.equals(type)) {
                            value = stringEncryptor.decrypt(realValue.toString());
                        } else {
                            value = stringEncryptor.encrypt(realValue.toString());
                        }
                        field.set(obj, value);
                    } else if (realValue instanceof List) {
                        List list = (List) realValue;
                        if (Objects.nonNull(list)) {
                            for (Object object : list) {
                                if (Objects.nonNull(object)) {
                                    handler(object,null, type);
                                }
                            }
                        }
                    } else if (realValue instanceof Set) {
                        Set set = (Set) realValue;
                        if (Objects.nonNull(set)) {
                            for (Object object : set) {
                                if (Objects.nonNull(object)) {
                                    handler(object,null, type);
                                }
                            }
                        }
                    } else if (realValue instanceof Map) {
                        Map map = (Map) realValue;
                        if (Objects.nonNull(map)) {
                            for (Object object : map.entrySet()) {
                                Map.Entry entry = (Map.Entry) object;
                                if (Objects.nonNull(entry.getValue())) {
                                    handler(entry.getValue(),null, type);
                                }
                            }
                        }
                    } else {
                                handler(realValue,null, type);
                        }
                    }
                }
            }
        return obj;
    }

    public String encryptValue(Object realValue) {
        String value = null;
        try {
            value = stringEncryptor.encrypt(String.valueOf(realValue));
        } catch (Exception ex) {
            return value;
        }
        return value;
    }

    public String decryptValue(Object realValue) {
        String value = String.valueOf(realValue);
        try {
            value = stringEncryptor.decrypt(value);
        } catch (Exception ex) {
            return value;
        }
        return value;
    }
}