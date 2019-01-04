package com.lyc.validator;

import com.lyc.mapper.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ClientValidator implements ConstraintValidator<UsernameDup,String> {

    @Autowired
    ClientMapper clientMapper;


    @Override
    public void initialize(UsernameDup usernameDup) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        try {
            if (clientMapper.selectByUsername(s) != null) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
