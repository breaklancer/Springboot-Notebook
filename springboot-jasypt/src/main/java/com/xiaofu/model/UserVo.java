package com.xiaofu.model;

import com.xiaofu.annotation.EncryptField;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: 公众号：程序员小富
 * @Date: 2021/7/26 15:10
 * @Description:
 */
@Data
@Builder
public class UserVo implements Serializable {

    private Long userId;

    @EncryptField
    private String mobile;

    @EncryptField
    private String address;

    private String age;

    @EncryptField
    private List<String> str1;
}
